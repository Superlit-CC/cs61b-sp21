package gitlet;

import java.io.File;
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
    public static void init() {
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
        File file = getFile(fileName);
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
     * 根据文件名从当前工作目录中生成File
     * @param fileName
     * @return File
     */
    private static File getFile(String fileName) {
        return Utils.join(Repository.CWD, fileName);
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
        Commit currentCommit = Commit.getCurrentCommit();
        // TODO: there is a bug!
        Commit newCommit = new Commit(message, currentCommit);
        // 将旧commit添加到新commit中
        newCommit.addBlobs(currentCommit.getPathToBlobID());
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
            Blob blob = Blob.readBlob(currentCommit.getBlobID(fileName));
            Stage removeStage = Stage.readRemoveStage();
            removeStage.add(fileName, blob.getBlobID());
            removeStage.saveRemoveStage();
            getFile(fileName).delete();
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

    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter. Hint: there is a useful
     * method in gitlet.Utils that will help you iterate over files within
     * a directory.
     */
    public static void global_log() {
        List<String> commitIDs = Utils.plainFilenamesIn(Repository.COMMITS);
        for (String commitID : commitIDs) {
            Commit commit = Commit.getCommit(commitID);
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
        String head = getCurrentBranchName();
        List<String> heads = Utils.plainFilenamesIn(Repository.HEADS);
        for (String h : heads) {
            if (head.equals(h)) {
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
        // 1. 当前commit中有，但工作目录中修改，但没有添加到add stage中 (modified)
        // 2. add stage中有，但工作区中的文件已经修改 (modified)
        // 3. add stage中有，但工作区中的文件已经删除 (deleted)
        // 4. 当前commit中有，remove stage中没有，但当前工作区已经删除 (deleted)
        Commit currentCommit = Commit.getCurrentCommit();
        // 获取所有当前commit追踪的文件名，处理情况1和4
        for (String fileName : currentCommit.getAllFilesSet()) {
            File file = getFile(fileName);
            // 情况1
            if (file.exists()) {
                Blob blob = new Blob(fileName);
                // 已经修改，且没有add
                if (!currentCommit.getBlobID(fileName).equals(blob.getBlobID()) &&
                        !blob.getBlobID().equals(addStage.getBlobID(fileName))) {
                    System.out.println(fileName + " (modified)");
                }
            } else if (removeStage.getBlobID(fileName) == null) {  // 情况4
                    System.out.println(fileName + " (deleted)");
            }
        }
        // 获取add stage中的所有文件名，处理情况2和3
        for (String fileName : addStage.getAllFilesSet()) {
            File file = getFile(fileName);
            // 情况2
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
        for (String fileName : getUntrackedFiles()) {
            System.out.println(fileName);
        }
    }

    /**
     * 获取当前commit中没有跟踪的文件
     * @return
     */
    private static List<String> getUntrackedFiles() {
        List<String> result = new ArrayList<>();
        Commit currentCommit = Commit.getCurrentCommit();
        Stage addStage = Stage.readAddStage();
        Stage removeStage = Stage.readRemoveStage();
        // 分两种情况（不考虑工作区的子目录）
        // 1. commit和add stage中都没有
        // 2. remove stage中有，但工作区中也有
        for (String fileName : Utils.plainFilenamesIn(Repository.CWD)) {
            File file = getFile(fileName);
            if ((currentCommit.getBlobID(fileName) == null && addStage.getBlobID(fileName) == null) ||
                    (removeStage.getBlobID(fileName) != null && file.exists())) {
                result.add(fileName);
            }
        }
        return result;
    }

    /**
     * Checkout Version 1
     * checkout -- [file name]
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
        blob.saveToCWD(fileName);
    }

    /**
     * Checkout Version 2
     * checkout [commit id] -- [file name]
     *
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
        blob.saveToCWD(fileName);
    }

    /**
     * Checkout Version 3
     * checkout [branch name]
     *
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
        // 异常1：判断是否是当前分支
        String currentBranchName = getCurrentBranchName();
        if (currentBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        // 异常2：获取指定branchID的File文件
        File branch = getBranchFile(branchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        // 异常3：未被当前commit追踪的文件将被被删除或覆盖
        Commit commit = Commit.getCommit(Utils.readContentsAsString(branch));
        checkUntrackedFiles(commit.getCommitID());
        // 回滚当前工作目录到指定分支
        rollbackToCommit(commit.getCommitID());
        // 修改当前分支
        changeHead(branch.getPath());
        // 清空stage
        Stage.clearRemoveStage();
        Stage.clearAddStage();
    }

    /**
     * 异常检查：There is an untracked file in the way; delete it, or add and commit it first.
     * @param commitID
     */
    private static void checkUntrackedFiles(String commitID) {
        List<String> untrackedFiles = getUntrackedFiles();
        Commit commit = Commit.getCommit(commitID);
        for (String fileName : untrackedFiles) {
            if (commit.getBlobID(fileName) != null) {
                // 被commit追踪且没有被当前分支追踪，报异常
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    /**
     * 将工作目录回滚到指定commit id
     * 不包括了文件的异常处理
     * @param commitID
     */
    private static void rollbackToCommit(String commitID) {
        // 处理该commit id下的文件
        // 1. 获取commit id和当前commit
        Commit commit = Commit.getCommit(commitID);
        // 2. commit中的追踪的文件，添加
        for (Map.Entry<String, String> pathToBlobID : commit.getPathToBlobID().entrySet()) {
            Blob blob = Blob.readBlob(pathToBlobID.getValue());
            blob.saveToCWD(pathToBlobID.getKey());
        }
        // 3. 不被指定commit追踪的文件，删除
        for (String fileName : Utils.plainFilenamesIn(Repository.CWD)) {
            if (commit.getBlobID(fileName) == null) {
                getFile(fileName).delete();
            }
        }
    }

    /**
     * 返回指定分支名的File
     * @param branchName
     * @return File
     */
    private static File getBranchFile(String branchName) {
        File branch = Utils.join(Repository.HEADS, branchName);
        return branch;
    }

    /**
     * 获取当前分支名
     * @return String
     */
    private static String getCurrentBranchName() {
        return getCurrentBranchFile().getName();
    }

    /**
     * Creates a new branch with the given name, and points it at the current
     * head commit. A branch is nothing more than a name for a reference
     * (a SHA-1 identifier) to a commit node. This command does NOT immediately
     * switch to the newly created branch (just as in real Git). Before you
     * ever call branch, your code should be running with a default branch
     * called “master”.
     *
     * @param branchName
     */
    public static void branch(String branchName) {
        // 1. 创建分支名为branchName的file
        File branch = getBranchFile(branchName);
        // 2. 处理重名异常
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        // 3. 将该分支指向当前commit
        Commit currentCommit = Commit.getCurrentCommit();
        Utils.writeContents(branch, currentCommit.getCommitID());
    }

    /**
     * Deletes the branch with the given name. This only means to delete the
     * pointer associated with the branch; it does not mean to delete all
     * commits that were created under the branch, or anything like that.
     *
     * @param branchName
     */
    public static void rmBranch(String branchName) {
        File branch = getBranchFile(branchName);
        // 1. 处理不存在异常
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // 2. 处理是当前分支异常
        if (getCurrentBranchName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        // 3. 删除该分支HEADS下的文件
        branch.delete();
    }

    /**
     * 获取当前分支的绝对路径
     * @return String
     */
    private static String getCurrentBranchAbsolutePath() {
        return Utils.readContentsAsString(Repository.HEAD);
    }

    /**
     * 获取当前分支
     * @return File
     */
    private static File getCurrentBranchFile() {
        String head = getCurrentBranchAbsolutePath();
        return new File(head);
    }

    /**
     * Checks out all the files tracked by the given commit. Removes tracked
     * files that are not present in that commit. Also moves the current branch’s
     * head to that commit node. See the intro for an example of what happens to
     * the head pointer after using reset. The [commit id] may be abbreviated as
     * for checkout. The staging area is cleared. The command is essentially
     * checkout of an arbitrary commit that also changes the current branch head.
     *
     * @param commitID
     */
    public static void reset(String commitID) {
        Commit commit = Commit.getCommit(commitID);
        // 异常1：没有该commit
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        // 异常2：有未追踪的文件
        checkUntrackedFiles(commitID);
        // 回滚
        rollbackToCommit(commitID);
        // 将当前分支指向当前commit
        Utils.writeContents(getCurrentBranchFile(), commitID);
        // 清空缓存区域
        Stage.clearAddStage();
        Stage.clearRemoveStage();
    }

    /**
     * Merges files from the given branch into the current branch.
     * @param branchName
     */
    public static void merge(String branchName) {
        // 先处理异常部分
        // 异常1：缓存区有文件
        Stage addStage = Stage.readAddStage();
        Stage removeStage = Stage.readRemoveStage();
        if (!addStage.isEmpty() || !removeStage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        // 异常2：给定分支存在
        File branchFile = getBranchFile(branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // 异常3：merge的分支是自己
        String currentBranchName = getCurrentBranchName();
        if (currentBranchName.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        Commit splitPoint = getBranchSplitPoint(currentBranchName, branchName);
        Commit other = Commit.getCommit(Utils.readContentsAsString(branchFile));
        Commit head = Commit.getCurrentCommit();

        // 异常4：未被当前commit追踪的文件将被被删除或覆盖
        checkUntrackedFiles(other.getCommitID());

        // 文件操作前的两个情况
        // 情况1：split point和other相同
        if (splitPoint.getCommitID().equals(other.getCommitID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
        } else if (splitPoint.getCommitID().equals(head.getCommitID())) {  // 情况2：split point和head相同
            checkoutV3(branchName);
            System.out.println("Current branch fast-forwarded.");
        } else {  // 文件操作
            // 储存到object、commit和工作目录中
            Map<String, Blob> savePathToBlobs = new HashMap<>();
            // 从commit和工作目录中删除
            Map<String, Blob> removePathToBlobs = new HashMap<>();
            // 1. 获取三个commit的所有文件名
            Set<String> allFiles = new HashSet<>();
            allFiles.addAll(splitPoint.getAllFilesSet());
            allFiles.addAll(other.getAllFilesSet());
            allFiles.addAll(head.getAllFilesSet());
            // 2. 依次判断情况
            for (String f : allFiles) {
                String sPBlobID = splitPoint.getBlobID(f);
                String otherBlobID = other.getBlobID(f);
                String headBlobID = head.getBlobID(f);

                if (sPBlobID == null) {  // 不在split point中
                    if (otherBlobID != null) {  // 在other中
                        if (headBlobID == null) {  // 不在head中
                            savePathToBlobs.put(f, Blob.readBlob(otherBlobID));
                        } else {  // 在head中
                            if (!otherBlobID.equals(headBlobID)) {  // 文件内容不同，冲突
                                savePathToBlobs.put(f, resolveConflict(headBlobID, otherBlobID));
                            }
                        }
                    }  // 不在other中的情况都是保持不变
                } else {  // 在split point中
                    if (otherBlobID == null) {  // 不在other中
                        if (headBlobID != null) {  // 在head中
                            if (sPBlobID.equals(headBlobID)) {  // 没有在head修改，移除
                                removePathToBlobs.put(f, Blob.readBlob(headBlobID));
                            } else {  // 在head中修改了，冲突
                                savePathToBlobs.put(f, resolveConflict(headBlobID, otherBlobID));
                            }
                        }
                    } else {  // 在other中
                        if (headBlobID == null) {  // 不在head中
                            if (!sPBlobID.equals(otherBlobID)) {  // other中修改了，冲突
                                savePathToBlobs.put(f, resolveConflict(headBlobID, otherBlobID));
                            }  // 否则other中没有修改，移除（保持不变）
                        } else {  // 在head中，判断修改情况
                            if (!sPBlobID.equals(otherBlobID)) {  // 在other中修改了
                                if (!sPBlobID.equals(headBlobID)) {  // 并且在head中也修改了
                                    if (!otherBlobID.equals(headBlobID)) {  // 并且修改内容不同，冲突
                                        savePathToBlobs.put(f, resolveConflict(headBlobID, otherBlobID));
                                    }  // 修改内容相同则保持不变
                                } else {  // 没在head中修改，保存为other
                                    savePathToBlobs.put(f, Blob.readBlob(otherBlobID));
                                }
                            }  // 没在other中修改，则保持不变
                        }
                    }
                }
            }
            // 文件的添加和删除
            for (Map.Entry<String, Blob> pathToBlob : savePathToBlobs.entrySet()) {
                Blob blob = pathToBlob.getValue();
                blob.save();
                blob.saveToCWD(pathToBlob.getKey());
                addStage.add(pathToBlob.getKey(), blob.getBlobID());
            }
            for (Map.Entry<String, Blob> pathToBlob : removePathToBlobs.entrySet()) {
                getFile(pathToBlob.getKey()).delete();
                removeStage.add(pathToBlob.getKey(), pathToBlob.getValue().getBlobID());
            }
            // 提交
            String message = "Merged " + branchName + " into " + currentBranchName + ".";
            Commit newCommit = new Commit(message, head, other);
            newCommit.addBlobs(head.getPathToBlobID());
            newCommit.addBlobs(addStage.getPathToBlobID());
            newCommit.removeBlobs(removeStage.getPathToBlobID());
            Stage.clearAddStage();
            Stage.clearRemoveStage();
            newCommit.save();
        }
    }

    private static Blob resolveConflict(String headBlobID, String otherBlobID) {
        byte[] b1 = "<<<<<<< HEAD\n".getBytes();
        byte[] b2 = "=======\n".getBytes();
        byte[] b3 = ">>>>>>>\n".getBytes();
        byte[] headBlobContent = new byte[0];
        byte[] otherBlobContent = new byte[0];
        if (headBlobID != null) {
            headBlobContent = Blob.readBlob(headBlobID).getContent();
        }
        if (otherBlobID != null) {
            otherBlobContent = Blob.readBlob(otherBlobID).getContent();
        }
        // b1 + headBlobContent + b2 + otherBlobContent + b3
        byte[] result = new byte[b1.length + headBlobContent.length +
                b2.length + otherBlobContent.length + b3.length];
        int len = 0;
        System.arraycopy(b1, 0, result, 0, b1.length);
        len += b1.length;
        System.arraycopy(headBlobContent, 0, result, len, headBlobContent.length);
        len += headBlobContent.length;
        System.arraycopy(b2, 0, result, len, b2.length);
        len += b2.length;
        System.arraycopy(otherBlobContent, 0, result, len, otherBlobContent.length);
        len += otherBlobContent.length;
        System.arraycopy(b3, 0, result, len, b3.length);
        System.out.println("Encountered a merge conflict.");
        return new Blob(result);
    }

    /**
     * 找到两个分支的split point（默认两个分支都存在）
     * @param branchName1
     * @param branchName2
     * @return Commit
     */
    private static Commit getBranchSplitPoint(String branchName1, String branchName2) {
        // 1. 获取两分支对应的commit
        Commit commit1 = Commit.getCommit(Utils.readContentsAsString(getBranchFile(branchName1)));
        Commit commit2 = Commit.getCommit(Utils.readContentsAsString(getBranchFile(branchName2)));
        // 2. 返回两个commit的最近公共祖先节点
        Commit result = Commit.getCommit(getCommitSplitPoint(commit1.getCommitID(), commit2.getCommitID()));
        return result;
    }

    /**
     * 找到两个commit的split point（默认两个commit都存在）
     * 也就是最近公共祖先节点
     * 这里用的算法就是找离 commitID1 最近的公共祖先节点
     * @param commitID1
     * @param commitID2
     * @return String
     */
    private static String getCommitSplitPoint(String commitID1, String commitID2) {
        Map<String, Integer> allAncestor1 = getAllAncestor(commitID1);
        Map<String, Integer> allAncestor2 = getAllAncestor(commitID2);
        int minDist = Integer.MAX_VALUE;
        String result = null;
        for (String commitID : allAncestor2.keySet()) {
            // 不是公共祖先节点，跳过
            if (!allAncestor1.containsKey(commitID)) {
                continue;
            }
            int dist = allAncestor1.get(commitID);
            if (dist < minDist) {
                minDist = dist;
                result = commitID;
            }
        }
        return result;
    }

    /**
     * 返回给定commit的所有祖先和对应的距离（包括自己）
     * @param commitID
     * @return Map<String, Integer>
     */
    private static Map<String, Integer> getAllAncestor(String commitID) {
        // BFS实现
        Map<String, Integer> result = new HashMap<>();
        Queue<Commit> q = new ArrayDeque<>();
        Commit commit = Commit.getCommit(commitID);
        q.add(commit);
        result.put(commitID, 0);
        while (!q.isEmpty()) {
            commit = q.remove();
            for (Commit c : commit.getParents()) {
                String id = c.getCommitID();
                if (result.containsKey(id)) {
                    continue;
                }
                result.put(id, result.get(commit.getCommitID()) + 1);
                q.add(c);
            }
        }
        return result;
    }
}


