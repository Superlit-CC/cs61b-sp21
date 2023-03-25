package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** 临时的CWD，用来测试。*/
//    public static final File CWD = Utils.join(new File(System.getProperty("user.dir")), "test");
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    public static final File REFS = join(GITLET_DIR, "refs");
    public static final File HEADS = join(REFS, "heads");
    public static final File MASTER = join(HEADS, "master");

    public static final File OBJECTS = join(GITLET_DIR, "objects");
    public static final File COMMITS = join(OBJECTS, "commits");
    public static final File BLOBS = join(OBJECTS, "blobs");

    public static final File ADD_STAGE = join(GITLET_DIR, "addstage");
    public static final File REMOVE_STAGE = join(GITLET_DIR, "removestage");
    public static final File HEAD = join(GITLET_DIR, "head");

    /**
     * 检验参数数量是否正确
     * 如果实际数量小于正确的数量，则打印异常，退出程序
     * @param x 正确的数量
     * @param y 实际的数量
     */
    public static void checkNumberOfArgs(int x, int y) {
        if (y < x) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * 检验目前工作区中是否包含.gitlet文件夹
     * 如果不包含则打印异常，退出程序
     */
    public static void checkGitlet() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
