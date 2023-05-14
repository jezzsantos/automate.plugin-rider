# Creating new dialogs

1. Create a new dialog class by adding a new file of the type 'New | Swing UI Designer | GUI Form'
2. Fill out the dialog:
    - `Form name`: <DIALOGNAME>
    - `Base layout manager`: GridLayoutManager (IntelliJ)
    - `Create bound class`: yes
    - `Class name`: <DIALOGNAME>

## Form

1. Rename the `JPanel` panel to `contents`
2. Start adding your controls in a simple grid
3. For `JLabel`s, make sure to remove the `Text` value, and set the `LabelFor` to the appropriate `JTextField`
4. For `JTextField`s make sure to remove the `Text` property
5. Make sure to leave a `VSpacer` at the bottom of the page, that spans all columns (stretch it left and right to fill
   all columns)

## Class

1. Extend `extends DialogWrapper`
2. In the constructor, add the following lines:
   ```java
    public <DIALOGNAME>(@NotNull Project project, @NotNull <DIALOGNAME>Context context) {
        super(project);

        this.context=context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.<DIALOGNAME>.Title"));
        }
   ```

3. Then, add statements to set up the other controls with their textual values. e.g.
   ```java
    this.nameTitle.setText(AutomateBundle.message("dialog.<DIALOGNAME>.Name.Title"));
    this.name.setText(this.context.getName());
   ```

4. Override the `createCenterPanel()` method:
   ```java
    @Override
    protected @Nullable JComponent createCenterPanel() {
   
    return this.contents;
    }
   ```
5. Override the `doValidate()` method, and call your own testable `doValidate()` method:
   ```java
    @Override
    protected @Nullable ValidationInfo doValidate() {

        return doValidate(this.context, this.name.getText());
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(@NotNull <DIALOGNAME>.<DIALOGNAME>Context context, @NotNull String name) {

        if (!name.isEmpty()) {
            if (!context.isValidName(name)) {
                return new ValidationInfo(AutomateBundle.message("dialog.<DIALOGNAME>.NameValidation.NotMatch.Message"));
            }
        }

        return null;
    }

   ```

6. Override the `doOKAction()` method, and be sure to set the context values, e.g
   ```java
    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.setName(this.name.getText());
    }
   ```
7. Add a new `getContext()` method, e.g
   ```java
    public <DIALOGNAME>.<DIALOGNAME>Context getContext() {

        return this.context;
    }
   ```