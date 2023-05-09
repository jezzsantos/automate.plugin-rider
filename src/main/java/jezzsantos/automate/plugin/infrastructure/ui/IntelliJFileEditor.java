package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.services.interfaces.IFileEditor;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class IntelliJFileEditor implements IFileEditor {

    private final Project project;

    @UsedImplicitly
    public IntelliJFileEditor(@NotNull Project project) {

        this.project = project;
    }

    @Override
    public boolean openFile(@NotNull String path) {

        var file = new File(path);
        if (!file.exists()) {
            return false;
        }

        var virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        if (virtualFile == null) {
            return false;
        }

        var editor = FileEditorManager.getInstance(this.project)
          .openFile(virtualFile, true);

        return editor.length > 0;
    }
}
