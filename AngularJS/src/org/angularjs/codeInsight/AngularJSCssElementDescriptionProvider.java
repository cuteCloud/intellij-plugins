package org.angularjs.codeInsight;

import com.intellij.lang.css.CssDialect;
import com.intellij.lang.css.CssDialectMappings;
import com.intellij.lang.javascript.psi.impl.JSOffsetBasedImplicitElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssElementDescriptorProvider;
import com.intellij.psi.css.CssSimpleSelector;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;
import com.intellij.xml.util.HtmlUtil;
import org.angularjs.index.AngularIndexUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Dennis.Ushakov
 */
public class AngularJSCssElementDescriptionProvider extends CssElementDescriptorProvider {
  @Override
  public boolean isMyContext(@Nullable PsiElement context) {
    if (context == null) return false;
    final PsiFile file = context.getContainingFile();
    if (file == null) return false;
    final Project project = context.getProject();
    if (HtmlUtil.hasHtml(file)) return AngularIndexUtil.hasAngularJS(project);
    final VirtualFile virtualFile = file.getOriginalFile().getVirtualFile();
    final CssDialect mapping = CssDialectMappings.getInstance(project).getMapping(virtualFile);
    return (mapping == null || mapping == CssDialect.CLASSIC) && AngularIndexUtil.hasAngularJS(project);
  }

  @Override
  public boolean isPossibleSelector(@NotNull final String selector, @NotNull PsiElement context) {
    return DirectiveUtil.getTagDirective(DirectiveUtil.normalizeAttributeName(selector), context.getProject()) != null;
  }

  @NotNull
  @Override
  public String[] getSimpleSelectors(@Nullable PsiElement context) {
    if (context == null) return ArrayUtil.EMPTY_STRING_ARRAY;
    final List<String> result = new LinkedList<String>();
    DirectiveUtil.processTagDirectives(context.getProject(), new Processor<JSOffsetBasedImplicitElement>() {
      @Override
      public boolean process(JSOffsetBasedImplicitElement proxy) {
        result.add(proxy.getName());
        return true;
      }
    });
    return ArrayUtil.toStringArray(result);
  }

  @NotNull
  @Override
  public PsiElement[] getDeclarationsForSimpleSelector(@NotNull CssSimpleSelector selector) {
    final JSOffsetBasedImplicitElement directive = DirectiveUtil.getTagDirective(DirectiveUtil.normalizeAttributeName(selector.getElementName()), selector.getProject());
    return directive != null ? new PsiElement[] {directive} : PsiElement.EMPTY_ARRAY;
  }
}
