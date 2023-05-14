package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.application.interfaces.patterns.CodeTemplate;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternFolderPlaceholderNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;

public class Selection {

    @Nullable
    public static PatternElement isPattern(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement pattern) {
                    if (pattern.isRoot()) {
                        return pattern;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static SelectedElement isElementOrPattern(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    if (patternElement.isRoot()) {
                        return new SelectedElement(patternElement, patternElement);
                    }
                    else {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedElement(parentElement, patternElement);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static SelectedElement isChildElementAndNotRoot(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    if (patternElement.isRoot()) {
                        return null;
                    }
                    else {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedElement(parentElement, patternElement);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static PatternElement isChildElementOrRootOrElementPlaceholder(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    return patternElement;
                }
                else {
                    if (leaf instanceof PatternFolderPlaceholderNode placeholder) {
                        return (placeholder.getChild() == placeholder.getParent().getElements())
                          ? placeholder.getParent()
                          : null;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static PatternElement isChildElementOrRootOrAttributePlaceholder(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    return patternElement;
                }
                else {
                    if (leaf instanceof PatternFolderPlaceholderNode placeholder) {
                        return (placeholder.getChild() == placeholder.getParent().getAttributes())
                          ? placeholder.getParent()
                          : null;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static PatternElement isChildElementOrRootOrCodeTemplatePlaceholder(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    return patternElement;
                }
                else {
                    if (leaf instanceof PatternFolderPlaceholderNode placeholder) {
                        return (placeholder.getChild() == placeholder.getParent().getCodeTemplates())
                          ? placeholder.getParent()
                          : null;
                    }
                }
            }
        }

        return null;
    }

    public static PatternElement isChildElementOrRootOrAutomationPlaceholder(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    return patternElement;
                }
                else {
                    if (leaf instanceof PatternFolderPlaceholderNode placeholder) {
                        return (placeholder.getChild() == placeholder.getParent().getAutomation())
                          ? placeholder.getParent()
                          : null;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static Selection.SelectedCodeTemplate isCodeTemplate(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof CodeTemplate codeTemplate) {
                    var parent = path.getParentPath().getParentPath().getLastPathComponent();
                    if (parent instanceof PatternElement parentElement) {
                        return new SelectedCodeTemplate(parentElement, codeTemplate);
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static Selection.SelectedAutomation isCodeTemplateCommandPlaceholder(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Automation automation) {
                    if (automation.getType() == AutomateConstants.AutomationType.CODE_TEMPLATE_COMMAND) {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedAutomation(parentElement, automation);
                        }
                    }
                }
            }
        }

        return null;
    }

    public static Selection.SelectedAutomation isCodeTemplateCommand(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Automation automation) {
                    if (automation.getType() == AutomateConstants.AutomationType.CODE_TEMPLATE_COMMAND) {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedAutomation(parentElement, automation);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static Selection.SelectedAutomation isCliCommand(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Automation automation) {
                    if (automation.getType() == AutomateConstants.AutomationType.CLI_COMMAND) {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedAutomation(parentElement, automation);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static Selection.SelectedAutomation isCommandLaunchPoint(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Automation automation) {
                    if (automation.getType() == AutomateConstants.AutomationType.COMMAND_LAUNCH_POINT) {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedAutomation(parentElement, automation);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static SelectedAttribute isAttribute(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Attribute attribute) {
                    var parent = path.getParentPath().getParentPath().getLastPathComponent();
                    if (parent instanceof PatternElement parentElement) {
                        return new SelectedAttribute(parentElement, attribute);
                    }
                }
            }
        }

        return null;
    }

    static class SelectedAttribute {

        private final Attribute attribute;
        private final PatternElement parent;

        public SelectedAttribute(@NotNull PatternElement parent, @NotNull Attribute attribute) {

            this.parent = parent;
            this.attribute = attribute;
        }

        public PatternElement getParent() {return this.parent;}

        public Attribute getAttribute() {return this.attribute;}
    }

    static class SelectedElement {

        private final PatternElement element;
        private final PatternElement parent;

        public SelectedElement(@NotNull PatternElement parent, @NotNull PatternElement element) {

            this.parent = parent;
            this.element = element;
        }

        public PatternElement getParent() {return this.parent;}

        public PatternElement getElement() {return this.element;}
    }

    static class SelectedCodeTemplate {

        private final CodeTemplate template;
        private final PatternElement parent;

        public SelectedCodeTemplate(@NotNull PatternElement parent, @NotNull CodeTemplate template) {

            this.parent = parent;
            this.template = template;
        }

        public PatternElement getParent() {return this.parent;}

        public CodeTemplate getTemplate() {return this.template;}
    }

    static class SelectedAutomation {

        private final Automation automation;
        private final PatternElement parent;

        public SelectedAutomation(@NotNull PatternElement parent, @NotNull Automation automation) {

            this.parent = parent;
            this.automation = automation;
        }

        public PatternElement getParent() {return this.parent;}

        public Automation getAutomation() {return this.automation;}
    }
}
