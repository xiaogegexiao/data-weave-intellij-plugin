// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

import java.util.List;

public class WeaveLambdaLiteralImpl extends WeaveLiteralExpressionImpl implements WeaveLambdaLiteral {

  public WeaveLambdaLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull WeaveVisitor visitor) {
    visitor.visitLambdaLiteral(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof WeaveVisitor) accept((WeaveVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public WeaveExpression getExpression() {
    return findChildByClass(WeaveExpression.class);
  }

  @Override
  @NotNull
  public List<WeaveFunctionParameter> getFunctionParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveFunctionParameter.class);
  }

  @Override
  @Nullable
  public WeaveType getType() {
    return findChildByClass(WeaveType.class);
  }

  @Override
  @NotNull
  public List<WeaveTypeParameter> getTypeParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, WeaveTypeParameter.class);
  }

}
