> IntelliJ Plugin Demo
> 
> 此Demo Plugin可应用于Idea社区版，Demo将提供变量重命名的功能。
> 1. 支持对选中变量的重命名。
> 2. 借助大语言模型，根据变量所在Java方法的上下文，生成新的变量名。

# Set up
+ install **plugin devkit** plugin in idea, then create project -> select IDE Plugin
+ clone plugin project from Github template (https://github.com/JetBrains/intellij-platform-plugin-template)

# Configuration

**build.gradle.kts**: 声明plugin的配置和提供gradle task能力


gradle task:
- runIde: run intellij application, used for test plugin
- buildPlugin: build plugin to jar

gradle plugin
https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html

idea版本
https://www.jetbrains.com/idea/download/other.html

**plugin.xml**: 声明plugin的配置和定义plugin的扩展

# Action
Action是Intellij菜单和工具栏对应按钮的行为

## Demo: Message Popup

```java
public class MessageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        new MessagesEx.MessageInfo(anActionEvent.getProject(), "world", "hello").showNow();
    }
}
```

# Editor

## Demo: Replace String
```java
public class MessageAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        SelectionModel selectionModel = editor.getSelectionModel();
        int startOffset = selectionModel.getSelectionStart();
        int endOffset = selectionModel.getSelectionEnd();

        WriteCommandAction.runWriteCommandAction(
                anActionEvent.getProject(),
                () -> editor.getDocument().replaceString(startOffset, endOffset, "hello")
        );
    }

}
```

# PSI Element
PSI: Program Structure Interface


## Demo: Enable Action for local variable 
```java
    @Override
    public void update(@NotNull AnActionEvent e) {

        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);

        if (psiElement instanceof PsiLocalVariable) {
            e.getPresentation().setEnabledAndVisible(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
```

# PSI Element query

## Demo: Replace PSI Element
```java
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        PsiElement currentElement = anActionEvent.getData(CommonDataKeys.PSI_ELEMENT);

        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(currentElement, PsiMethod.class);
        PsiCodeBlock psiCodeBlock = PsiTreeUtil.findChildOfType(psiMethod, PsiCodeBlock.class);
        Collection<PsiIdentifier> psiIdentifiers = PsiTreeUtil.findChildrenOfType(psiCodeBlock, PsiIdentifier.class);

        Project project = anActionEvent.getProject();

        String variableName = ((PsiLocalVariable) currentElement).getName();
        String newVariableName = "hello";

        WriteCommandAction.runWriteCommandAction(project, () -> {
            psiIdentifiers.stream()
                    .filter(psiIdentifier -> psiIdentifier.getText().equals(variableName))
                    .forEach(psiIdentifier -> {

                        PsiIdentifier newIdentifier = JavaPsiFacade.getInstance(project).getElementFactory().createIdentifier(newVariableName);
                        psiIdentifier.replace(newIdentifier);
                    });
        });

    }
```

# LLM
run ollama in local

## Demo: LLM
```java
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        PsiElement currentElement = anActionEvent.getData(CommonDataKeys.PSI_ELEMENT);

        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(currentElement, PsiMethod.class);
        PsiCodeBlock psiCodeBlock = PsiTreeUtil.findChildOfType(psiMethod, PsiCodeBlock.class);
        Collection<PsiIdentifier> psiIdentifiers = PsiTreeUtil.findChildrenOfType(psiCodeBlock, PsiIdentifier.class);

        Project project = anActionEvent.getProject();

        String variableName = ((PsiLocalVariable) currentElement).getName();
        String newVariableName = new ChatService().generateVariableName(psiMethod.getText(), variableName);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            psiIdentifiers.stream()
                    .filter(psiIdentifier -> psiIdentifier.getText().equals(variableName))
                    .forEach(psiIdentifier -> {

                        PsiIdentifier newIdentifier = JavaPsiFacade.getInstance(project).getElementFactory().createIdentifier(newVariableName);
                        psiIdentifier.replace(newIdentifier);
                    });
        });

    }
```