package org.mule.tooling.lang.dw.wizard;

import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;
import org.mule.tooling.lang.dw.WeaveIcons;

import javax.swing.*;
import java.io.File;

public class WeaveModuleBuilder extends MavenModuleBuilder implements SourcePathsBuilder {

    private String weaveVersion = "2.1.4-SNAPSHOT";


    public WeaveModuleBuilder() {
        setProjectId(new MavenId("org.mule.weave.module", "my-weave-module", "1.0.0-SNAPSHOT"));
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);

        final Project project = rootModel.getProject();
        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);

        //Check if this is a module and has parent
        final MavenId parentId = (this.getParentProject() != null ? this.getParentProject().getMavenId() : null);

        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {
            WeaveModuleInitializer.configure(project, getProjectId(), weaveVersion, root, parentId);
        });
    }

    private VirtualFile createAndGetContentEntry() {
        String path = FileUtil.toSystemIndependentName(this.getContentEntryPath());
        (new File(path)).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public String getName() {
        return "Weave Module";
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }


    @Override
    public String getPresentableName() {
        return "Weave Module";
    }

    @Override
    public Icon getNodeIcon() {
        return WeaveIcons.DataWeaveIcon;
    }

    @Override
    public String getDescription() {
        return "Create a Weave Module. ";
    }

//    @Nullable
//    @Override
//    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
//        MuleVersionConfiguration step = new MuleVersionConfiguration(this, muleVersion);
//        Disposer.register(parentDisposable, step);
//        return step;
//    }


}
