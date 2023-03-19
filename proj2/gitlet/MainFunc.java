package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Implement the functions of Main class.
 *
 * @author superlit
 * @create 2023/3/13 20:30
 */
public class MainFunc {
    /**
     * Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit: a commit that
     * contains no files and has the commit message initial commit (just like
     * that, with no punctuation). It will have a single branch: master,
     * which initially points to this initial commit, and master will be the
     * current branch. The timestamp for this initial commit will be 00:00:00 UTC,
     * Thursday, 1 January 1970 in whatever format you choose for dates
     * (this is called “The (Unix) Epoch”, represented internally by the time 0.)
     * Since the initial commit in all repositories created by Gitlet will have
     * exactly the same content, it follows that all repositories will
     * automatically share this commit (they will all have the same UID) and
     * all commits in all repositories will trace back to it.
     */
    public static void init() throws IOException {
        if (Repository.GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
            Repository.GITLET_DIR.mkdir();
            // .gitlet/objects/
            Repository.OBJECTS.mkdir();
            // .gitlet/objects/commits/
            Repository.COMMITS.mkdir();
            // .gitlet/objects/blobs
            Repository.BLOBS.mkdir();
            // .gitlet/refs/
            Repository.REFS.mkdir();
            // .gitlet/refs/heads
            Repository.HEADS.mkdir();
            Commit commit = new Commit();
            // .gitlet/head
            changeHead(Repository.MASTER.getPath());
            // 保存初始commit到head指定分支中
            commit.save();
            // .gitlet/addstage
            Utils.writeObject(Repository.ADD_STAGE, new Stage());
            // .gitlet/removestage
            Utils.writeObject(Repository.REMOVE_STAGE, new Stage());
        }
    }

    /** 将当前head指针指向改为指定headPath */
    private static void changeHead(String headPath) {
        Utils.writeContents(Repository.HEAD, headPath);
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area (see the
     * description of the commit command). For this reason, adding a file is also
     * called staging the file for addition. Staging an already-staged file overwrites
     * the previous entry in the staging area with the new contents. The staging area
     * should be somewhere in .gitlet. If the current working version of the file is
     * identical to the version in the current commit, do not stage it to be added,
     * and remove it from the staging area if it is already there (as can happen
     * when a file is changed, added, and then changed back to it’s original version).
     * The file will no longer be staged for removal (see gitlet rm), if it was at
     * the time of the command.
     */
    public static void add(String fileName) {
        File file = Utils.join(Repository.CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Commit commit = Commit.getCurrentCommit();
        Blob blob = new Blob(fileName);
        blob.save();  // 修改一行就会存储到blobs中，但不一定会最终commit
        Stage addStage = Stage.readAddStage();
        // 判断该blob的内容相比较当前commit的内容是否修改过
        if (blob.getBlobID().equals(commit.getBlobID(fileName))) {
            addStage.remove(fileName);
        } else {
            addStage.add(fileName, blob.getBlobID());
        }
        addStage.saveAddStage();
        // 将其从remove stage中移除
        Stage removeStage = Stage.readRemoveStage();
        if (removeStage.getBlobID(fileName) != null) {
            removeStage.remove(fileName);
        }
        removeStage.saveRemoveStage();
    }

    /**
     * Saves a snapshot of tracked files in the current commit and staging area
     * so they can be restored at a later time, creating a new commit. The
     * commit is said to be tracking the saved files. By default, each commit’s
     * snapshot of files will be exactly the same as its parent commit’s snapshot
     * of files; it will keep versions of files exactly as they are, and not
     * update them. A commit will only update the contents of files it is tracking
     * that have been staged for addition at the time of commit, in which case
     * the commit will now include the version of the file that was staged instead
     * of the version it got from its parent. A commit will save and start tracking
     * any files that were staged for addition but weren’t tracked by its parent.
     * Finally, files tracked in the current commit may be untracked in the new
     * commit as a result being staged for removal by the rm command (below).
     */
    public static void commit(String message) {
        Stage addStage = Stage.readAddStage();
        Stage removeStage = Stage.readRemoveStage();
        if (addStage.isEmpty() && removeStage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        // 创建一个新的commit，指向当前commit
        Commit newCommit = new Commit(message, Commit.getCurrentCommit());
        // 将add stage中的blobs添加至新的commit当中
        newCommit.addBlobs(addStage.getPathToBlobID());
        // 移除remove stage中的blobs
        newCommit.removeBlobs(removeStage.getPathToBlobID());
        // 清空stage
        Stage.clearAddStage();
        Stage.clearRemoveStage();
        // 保存新的commit
        newCommit.save();
    }

    /**
     * Unstage the file if it is currently staged for addition. If the file
     * is tracked in the current commit, stage it for removal and remove
     * the file from the working directory if the user has not already done
     * so (do not remove it unless it is tracked in the current commit).
     * @param fileName
     */
    public static void rm(String fileName) {
        // 如果已经在add stage中，移除添加
        Stage addStage = Stage.readAddStage();
        Commit currentCommit = Commit.getCurrentCommit();
        if (addStage.getBlobID(fileName) == null && currentCommit.getBlobID(fileName) == null) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        addStage.remove(fileName);
        addStage.saveAddStage();
        // 如果在当前commit中，存入remove stage中准备删除，并将工作目录中该文件删除
        // 如果不在当前commit中就不要删除工作目录中的文件
        if (currentCommit.getBlobID(fileName) != null) {
            Blob blob = new Blob(fileName);
            Stage removeStage = Stage.readRemoveStage();
            removeStage.add(fileName, blob.getBlobID());
            removeStage.saveRemoveStage();
            new File(fileName).delete();
        }
    }

    /**
     * Starting at the current head commit, display information about each
     * commit backwards along the commit tree until the initial commit,
     * following the first parent commit links, ignoring any second parents
     * found in merge commits. (In regular Git, this is what you get with
     * git log --first-parent). This set of commit nodes is called the
     * commit’s history. For every node in this history, the information
     * it should display is the commit id, the time the commit was made,
     * and the commit message.
     */
    public static void log() {
        // 如果.gitlet不存在则不处理
        if (Repository.GITLET_DIR.exists()) {
            Commit currentCommit = Commit.getCurrentCommit();
            do {
                List<Commit> parents = currentCommit.getParents();
                currentCommit.displayLog();
                if (parents.size() == 0) {
                    break;
                }
                currentCommit = parents.get(0);
            } while (true);
        }
    }

    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter. Hint: there is a useful
     * method in gitlet.Utils that will help you iterate over files within
     * a directory.
     */
    public static void global_log() {
        List<String> commitNames = Utils.plainFilenamesIn(Repository.COMMITS);
        for (String commitName : commitNames) {
            Commit commit = Utils.readObject(Utils.join(Repository.COMMITS, commitName), Commit.class);
            commit.displayLog();
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message,
     * one per line. If there are multiple such commits, it prints the ids
     * out on separate lines. The commit message is a single operand; to
     * indicate a multiword message, put the operand in quotation marks,
     * as for the commit command below. Hint: the hint for this command is
     * the same as the one for global-log.
     */
    public static void find(String message) {
        boolean flag = false;
        List<String> commitNames = Utils.plainFilenamesIn(Repository.COMMITS);
        for (String commitName : commitNames) {
            Commit commit = Utils.readObject(Utils.join(Repository.COMMITS, commitName), Commit.class);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getCommitID());
                flag = true;
            }
        }
        if (flag == false) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /**
     * Displays what branches currently exist, and marks the current branch
     * with a *. Also displays what files have been staged for addition or removal.
     */
    public static void status() {
        System.out.println("=== Branches ===");
        String head = Utils.readContentsAsString(Repository.HEAD);
        List<String> heads = Utils.plainFilenamesIn(Repository.HEADS);
        for (String h : heads) {
            if (head.equals(Utils.join(Repository.HEADS, h).getPath())) {
                System.out.print("*");
            }
            System.out.println(h);
        }

        System.out.println("\n=== Staged Files ===");
        Stage addStage = Stage.readAddStage();
        Map<String, String> pathToBlobID = addStage.getPathToBlobID();
        for (String key : pathToBlobID.keySet()) {
            System.out.println(key);
        }

        System.out.println("\n=== Removed Files ===");
        Stage removeStage = Stage.readRemoveStage();
        pathToBlobID = removeStage.getPathToBlobID();
        for (String key : pathToBlobID.keySet()) {
            System.out.println(key);
        }

        System.out.println("\n=== Modifications Not Staged For Commit ===");
        // 分四种情况：
        // 1. 当前commit中有，但已经修改且未上传到add stage中
        // 2. add stage中有，但工作区中的文件已经修改
        // 3. add stage中有，但工作区中的文件已经删除
        // 4. remove stage中没有，且当前commit中有，但当前工作区已经删除
        Commit currentCommit = Commit.getCurrentCommit();
        // 获取所有当前commit追踪的文件名，处理情况1和4
        for (String fileName : currentCommit.getAllFilesSet()) {
            File file = Utils.join(Repository.CWD, fileName);
            // 该文件存在，情况1
            if (file.exists()) {
                Blob blob = new Blob(fileName);
                String blobID = addStage.getBlobID(fileName);
                if (blobID != null && (!blobID.equals(blob.getBlobID()))) {
                    System.out.println(fileName + " (modified)");
                }
            } else {  // 如果该文件不存在（已被删除），情况4
                // 是否被添加到remove stage中
                if (removeStage.getBlobID(fileName) == null) {
                    System.out.println(fileName + " (deleted)");
                }
            }
        }
        // 获取add stage中的所有文件名，处理情况2和3
        for (String fileName : addStage.getAllFilesSet()) {
            File file = Utils.join(Repository.CWD, fileName);
            // 文件存在，情况2
            if (file.exists()) {
                // 是否被修改
                Blob blob = new Blob(fileName);
                if (!addStage.getBlobID(fileName).equals(blob.getBlobID())) {
                    System.out.println(fileName + " (modified)");
                }
            } else {  // 文件已经删除，情况3
                System.out.println(fileName + " (deleted)");
            }
        }

        System.out.println("\n=== Untracked Files ===");
        // 分两种情况（不考虑工作区的子目录）
        // 1. commit和add stage中都没有
        // 2. remove stage中有，但工作区中也有
        for (String fileName : Utils.plainFilenamesIn(Repository.CWD)) {
            File file = Utils.join(Repository.CWD, fileName);
            if ((currentCommit.getBlobID(fileName) == null && addStage.getBlobID(fileName) == null) ||
                    (removeStage.getBlobID(fileName) != null && file.exists())) {
                System.out.println(fileName);
            }
        }
    }

    /**
     * Checkout Version 1
     * Takes the version of the file as it exists in the head commit and
     * puts it in the working directory, overwriting the version of the
     * file that’s already there if there is one. The new version of the
     * file is not staged.
     *
     * @param fileName
     */
    public static void checkoutV1(String fileName) {
        Commit currentCommit = Commit.getCurrentCommit();
        Blob blob = currentCommit.getBlob(fileName);
        if (blob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        blob.saveToCWD();
    }

    /**
     * Checkout Version 2
     * Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. The new version of the file is not staged.
     *
     * @param commitID
     * @param fileName
     */
    public static void checkoutV2(String commitID, String fileName) {
        Commit commit = Commit.getCommit(commitID);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Blob blob = commit.getBlob(fileName);
        if (blob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        blob.saveToCWD();
    }

    /**
     * Checkout Version 3
     * Takes all files in the commit at the head of the given branch, and puts
     * them in the working directory, overwriting the versions of the files
     * that are already there if they exist. Also, at the end of this command,
     * the given branch will now be considered the current branch (HEAD). Any
     * files that are tracked in the current branch but are not present in the
     * checked-out branch are deleted. The staging area is cleared, unless the
     * checked-out branch is the current branch.
     *
     * @param branchName
     */
    public static void checkoutV3(String branchName) {
        // 判断是否是当前分支
        String currentBranchName = getCurrentBranchName();
        if (currentBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        boolean flag = false;
        List<Blob> saveBlobs = new ArrayList<>();
        List<File> deleteFiles = new ArrayList<>();
        for (String branch : Utils.plainFilenamesIn(Repository.HEADS)) {
            if (branch.equals(branchName)) {
                // 处理该分支下的文件
                // 1. 获取checkout分支下的commit和当前commit
                String commitID = Utils.readContentsAsString(Utils.join(Repository.HEADS, branchName));
                Commit commit = Commit.getCommit(commitID);
                Commit currentCommit = Commit.getCurrentCommit();
                // 2. 获取checkout分支commit追踪的blobs
                for (String blobID : commit.getAllBlobsID()) {
                    // 3. 将blobs的内容储存到对应的文件名下（需要处理异常）
                    Blob blob = Blob.readBlob(blobID);
                    if (currentCommit.getBlobID(blob.getFileName()) == null) {
                        // 被checkout追踪且没有被当前分支追踪，报异常
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                    // 被两个commit都追踪，储存文件
                    saveBlobs.add(blob);
                }
                // 4. 被二者都不追踪的文件，或者只被当前commit追踪，删除
                for (String fileName : Utils.plainFilenamesIn(Repository.CWD)) {
                    if (currentCommit.getBlobID(fileName) == null && commit.getBlobID(fileName) == null) {
                        deleteFiles.add(Utils.join(Repository.CWD, fileName));
                    }
                }
                flag = true;
                break;
            }
        }
        // 没有该分支
        if (!flag) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        // 修改当前分支
        changeHead(Utils.join(Repository.HEADS, branchName).getPath());
        // 添加和删除文件
        for (Blob blob : saveBlobs) {
            blob.saveToCWD();
        }
        for (File file : deleteFiles) {
            file.delete();
        }
        // 清空stage
        Stage.clearRemoveStage();
        Stage.clearAddStage();
    }

    /** 获取当前分支名 */
    private static String getCurrentBranchName() {
        // 当前分支的绝对路径
        String head = Utils.readContentsAsString(Repository.HEAD);
        return new File(head).getName();
    }
}
