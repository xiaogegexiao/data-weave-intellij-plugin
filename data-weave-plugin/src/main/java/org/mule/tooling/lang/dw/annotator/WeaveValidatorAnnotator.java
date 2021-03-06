package org.mule.tooling.lang.dw.annotator;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.IJWeaveTextDocument;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;
import org.mule.tooling.lang.dw.service.WeaveRuntimeContextManager;
import org.mule.tooling.lang.dw.util.AsyncCache;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.editor.QuickFix;
import org.mule.weave.v2.editor.ValidationMessage;
import org.mule.weave.v2.editor.ValidationMessages;
import org.mule.weave.v2.parser.location.Position;
import org.mule.weave.v2.parser.location.WeaveLocation;

public class WeaveValidatorAnnotator extends ExternalAnnotator<PsiFile, ValidationMessages> {
  private AsyncCache<PsiFile, ValidationMessages> cache;

  @Nullable
  @Override
  public PsiFile collectInformation(@NotNull PsiFile file) {
    if (file.getFileType() == WeaveFileType.getInstance()) {
      return file;
    } else {
      return null;
    }
  }

  @Nullable
  @Override
  public ValidationMessages doAnnotate(PsiFile file) {
    WeaveDocument weaveDocument = ReadAction.compute(() -> WeavePsiUtils.getWeaveDocument(file));
    if (weaveDocument == null) return null;
    Project project = file.getProject();
    if (project.isDisposed()) return null;

    WeaveRuntimeContextManager scenariosManager = WeaveRuntimeContextManager.getInstance(project);
    WeaveEditorToolingAPI toolingAPI = WeaveEditorToolingAPI.getInstance(project);
    ImplicitInput currentImplicitTypes = ReadAction.compute(() -> scenariosManager.getImplicitInputTypes(weaveDocument));
    if (weaveDocument.isModuleDocument() || currentImplicitTypes != null) {
      return toolingAPI.typeCheck(file);
    } else {
      if (cache == null) {
        cache = new AsyncCache<>(toolingAPI::parseCheck);
        toolingAPI.addOnCloseListener(() -> {
          // throw away cache on project close.
          cache = null;
        });
      }
      return cache.resolve(file).orElse(null);
    }
  }

  @Override
  public void apply(@NotNull PsiFile file, ValidationMessages annotationResult, @NotNull AnnotationHolder holder) {
    ValidationMessage[] errorMessage = annotationResult.errorMessage();
    apply(holder, errorMessage, HighlightSeverity.ERROR);
    ValidationMessage[] validationMessages = annotationResult.warningMessage();
    apply(holder, validationMessages, HighlightSeverity.WARNING);

  }

  public void apply(@NotNull AnnotationHolder holder, ValidationMessage[] errorMessage, HighlightSeverity severity) {
    for (ValidationMessage validationMessage : errorMessage) {
      WeaveLocation location = validationMessage.location();
      int startIndex = getValidIndex(location.startPosition());
      int endIndex = getValidIndex(location.endPosition());
      Annotation annotation = holder.createAnnotation(severity, new TextRange(startIndex, endIndex), validationMessage.message().message(), WeaveEditorToolingAPI.toHtml(validationMessage.message().message()));
      QuickFix[] quickFixes = validationMessage.quickFix();
      for (QuickFix quickFix : quickFixes) {
        annotation.registerFix(new WeaveIntentionAction(quickFix));
      }

    }
  }

  private int getValidIndex(Position position) {
    int index = position.index();
    if (index < 0) {
      return 0;
    } else {
      return index;
    }
  }

  private static class WeaveIntentionAction implements IntentionAction {

    private QuickFix quickFix;

    public WeaveIntentionAction(QuickFix quickFix) {
      this.quickFix = quickFix;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
      return quickFix.name();
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
      return quickFix.description();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
      return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
      quickFix.quickFix().run(new IJWeaveTextDocument(editor, project));
    }

    @Override
    public boolean startInWriteAction() {
      return false;
    }

  }

}
