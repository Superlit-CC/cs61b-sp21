package gitlet;

import gitlet.Main;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author superlit
 * @create 2023/3/14 22:09
 */
public class MainTest {
    @Test
    public void testInit() {
        String[] args = new String[1];
        args[0] = "init";
        Main.main(args);
    }

    @Test
    public void testAdd() {
        String[] args = new String[2];
        args[0] = "add";
        args[1] = "f.txt";
        Main.main(args);
    }

    @Test
    public void testCommit() {
        String[] args = new String[2];
        args[0] = "commit";
        args[1] = "wug.txt";
        Main.main(args);
    }

    @Test
    public void testRM() {
        String[] args = new String[2];
        args[0] = "rm";
        args[1] = "wug.txt";
        Main.main(args);
    }

    @Test
    public void testLog() {
        String[] args = new String[1];
        args[0] = "log";
        Main.main(args);
    }

    @Test
    public void testGlobalLog() {
        String[] args = new String[1];
        args[0] = "global-log";
        Main.main(args);
    }

    @Test
    public void testFind() {
        String[] args = new String[2];
        args[0] = "find";
        args[1] = "wug.txt";
        Main.main(args);
    }

    @Test
    public void testStatus() {
        String[] args = new String[1];
        args[0] = "status";
        Main.main(args);
    }

    @Test
    public void testCheckoutV1() {
        String[] args = new String[3];
        args[0] = "checkout";
        args[1] = "--";
        args[2] = "g.txt";
        Main.main(args);
    }

    @Test
    public void testCheckoutV2() {
        String[] args = new String[4];
        args[0] = "checkout";
        args[1] = "0b5d68999cc3103897abe1ef7492b0128ecfe6d5";
        args[2] = "++";
        args[3] = "wug.txt";
        Main.main(args);
    }

    @Test
    public void testCheckoutV3() {
        String[] args = new String[2];
        args[0] = "checkout";
        args[1] = "hhh";
        Main.main(args);
    }

    @Test
    public void testBranch() {
        String[] args = new String[2];
        args[0] = "branch";
        args[1] = "branch1";
        Main.main(args);
    }

    @Test
    public void testRMBranch() {
        String[] args = new String[2];
        args[0] = "rm-branch";
        args[1] = "branch1";
        Main.main(args);
    }

    @Test
    public void testReset() {
        String[] args = new String[2];
        args[0] = "reset";
        args[1] = "ada7d738b9f2312ae583c18fca0d7b5eaad836af";
        Main.main(args);
    }

    @Test
    public void testMerge() {
        String[] args = new String[2];
        args[0] = "merge";
        args[1] = "branch1";
        Main.main(args);
    }

    @Test
    public void test2() {
        prelude1();
        writeContentsToCWD("wug.txt", "wug.txt");
        add("wug.txt");
        commit("added wug");
        writeContentsToCWD("wug.txt", "notwug.txt");
        checkoutV1("wug.txt");
        // TODO: wug.txt内容应该为wug.txt
        // 字符串没有用equals判断相等
    }

    @Test
    public void test13() {
        setup2();
        // 删除 f.txt，检查工作目录应该不存在该文件
        rm("f.txt");
        status();
    }

    @Test
    public void test15() {
        setup2();
        rm("f.txt");
        writeContentsToCWD("f.txt", "wug.txt");
        add("f.txt");
        status();
        // 输出白板
    }

    @Test
    public void test18() {
        setup2();
        add("f.txt");
        status();
        // 输出白板
    }

    @Test
    public void test20() {
        setup2();
        status();  // 输出白板
        rm("f.txt");
        commit("Removed f.txt");
        status();  // 输出白板
    }

    @Test
    public void test22() {
        setup2();
        Utils.join(Repository.CWD, "f.txt").delete();
        rm("f.txt");
        status();
    }

    @Test
    public void test24() {
        setup2();
        writeContentsToCWD("h.txt", "wug.txt");
        add("h.txt");
        commit("Add h");
        log();
        // TODO: reset()
        // TODO: global-log()
    }

    @Test
    public void test29() {
        prelude1();
        writeContentsToCWD("wug.txt", "wug.txt");
        add("wug.txt");
        commit("version 1 of wug.txt");
        writeContentsToCWD("wug.txt", "notwug.txt");
        add("wug.txt");
        commit("version 2 of wug.txt");
        log();
        // TODO: checkout ${UID2} -- warg.txt
        // File does not exist in that commit.
        // TODO: checkout 5d0bc169a1737e955f9cb26b9e7aa21e4afd4d12 -- wug.txt
        // No commit with that id exists.
        // TODO: checkout ${UID2} ++ wug.txt
        // Incorrect operands.
        // TODO: checkout foobar
        // No such branch exists.
        // TODO: checkout master
        // No need to checkout the current branch.
    }

    @Test
    public void test30_1() {
        prelude1();
        branch("other");
        writeContentsToCWD("f.txt", "wug.txt");
        writeContentsToCWD("g.txt", "notwug.txt");
        add("g.txt");
        add("f.txt");
        commit("Main two files");
        checkoutV3("other");
        // TODO: f和g应该不在工作区
    }

    @Test
    public void test30_2() {
        writeContentsToCWD("f.txt", "notwug.txt");
        add("f.txt");
        commit("Alternative file");
        // TODO: f内容应该为notwug.txt，g不存在
    }

    @Test
    public void test30_3() {
//        checkoutV3("master");
        checkoutV3("other");
        // TODO: 分别查看上面两个操作之后的结果
        // *Blob类中有fileName属性，而一个blob可能对应多个file，这会导致很多问题
    }

    @Test
    public void test34_1() {
        setup2();
        branch("other");
        writeContentsToCWD("h.txt", "wug2.txt");
        add("h.txt");
        rm("g.txt");
        // 两次commit的f.txt的内容相同
        writeContentsToCWD("f.txt", "wug2.txt");
        add("f.txt");
        commit("Add h.txt, remove g.txt, and change f.txt");
        checkoutV3("other");
        writeContentsToCWD("f.txt", "notwug.txt");
        add("f.txt");
        writeContentsToCWD("k.txt", "wug3.txt");
        add("k.txt");
        commit("Add k.txt and modify f.txt");
        checkoutV3("master");
        log();
        merge("other");
        /*
        * g.txt
        = h.txt wug2.txt
        = k.txt wug3.txt
        = f.txt conflict1.txt
         */
    }

    @Test
    public void test34_2() {
        log();
        status();
    }

    @Test
    public void test38_1() {
        setup2();
        branch("other");
        writeContentsToCWD("h.txt", "wug2.txt");
        add("h.txt");
        rm("g.txt");
        commit("Add h.txt and remove g.txt");
        log();
        checkoutV3("other");
        reset("reset 025052f2b193d417df998517a4c539918801b430");
        // TODO: 输出 No commit with that id exists.
    }

    @Test
    public void test38_2() {
        writeContentsToCWD("h.txt", "wug3.txt");
        // TODO: reset [master] 输出 There is an untracked file in the way; delete it, or add and commit it first.
        reset("7c59b436c05fb4433c936f4cfc98a9f73cdb97d9");
        // 没有上面的异常处理
    }

    @Test
    public void test40_1() {
        setup2();
        branch("b1");
        writeContentsToCWD("h.txt", "wug2.txt");
        add("h.txt");
        commit("Add h.txt");
        branch("b2");
        rm("f.txt");
        commit("remove f.txt");
        merge("b1");
        // TODO: 输出 Given branch is an ancestor of the current branch.
    }

    @Test
    public void test40_2() {
        checkoutV3("b2");
        // TODO: = f.txt wug.txt
        merge("master");
        // TODO: 输出 Current branch fast-forwarded.
        // TODO: * f.txt
    }

    @Test
    public void test42_1() {
        String[] args = new String[0];
        Main.main(args);
    }
    @Test
    public void test42_2() {
        status();
    }

    @Test
    public void test43_1() {
        prelude1();
        branch("given");
        writeContentsToCWD("f.txt", "wug.txt\n");
        add("f.txt");
        commit("Add f.txt containing wug.txt");
        checkoutV3("given");
        writeContentsToCWD("f.txt", "notwug.txt\n");
        add("f.txt");
        commit("Add f.txt containing notwug.txt");
        branch("B");
        merge("master");
        // TODO: 输出 Encountered a merge conflict.
        // TODO: = f.txt conflict3.txt
    }
    @Test
    public void test43_2() {
        writeContentsToCWD("f.txt", "notwug.txt\n");
        add("f.txt");
        commit("Reset f to notwug.txt");
        rm("f.txt");
        commit("given now empty.");
        checkoutV3("master");
        writeContentsToCWD("g.txt", "wug2.txt\n");
        add("g.txt");
        commit("Added g.txt");
        merge("B");
        // TODO: Encountered a merge conflict.
        // TODO: = f.txt conflict4.txt
    }
    @Test
    public void test43_3() {
        writeContentsToCWD("f.txt", "wug.txt");
        add("f.txt");
        commit("Reset f to wug.txt");
        merge("given");
        // TODO: Encountered a merge conflict.
        // TODO: = f.txt conflict5.txt
        // TODO: = g.txt wug2.txt
    }

    private void prelude1() {
        String[] args = new String[1];
        args[0] = "init";
        Main.main(args);
    }

    private void setup1() {
        writeContentsToCWD("f.txt", "wug.txt");
        writeContentsToCWD("g.txt", "notwug.txt");
        prelude1();
        add("f.txt");
        add("g.txt");
    }

    private void writeContentsToCWD(String fileName, String contents) {
        File f = Utils.join(Repository.CWD, fileName);
        Utils.writeContents(f, contents);
    }

    private void setup2() {
        setup1();
        String[] args = new String[2];
        args[0] = "commit";
        args[1] = "Two files";
        Main.main(args);
    }

    private void setup3() {
        prelude1();
        branch("Empty");
        writeContentsToCWD("f.txt", "text1.txt");
        writeContentsToCWD("g.txt", "wug.txt");
        writeContentsToCWD("h.txt", "notwug.txt");
        add("g.txt");
        add("f.txt");
        add("h.txt");
        commit("Three files");
        branch("B1");
    }

    private void branch(String branchName) {
        String[] args = new String[2];
        args[0] = "branch";
        args[1] = branchName;
        Main.main(args);
    }

    private void add(String fileName) {
        String[] args = new String[2];
        args[0] = "add";
        args[1] = fileName;
        Main.main(args);
    }

    private void commit(String message) {
        String[] args = new String[2];
        args[0] = "commit";
        args[1] = message;
        Main.main(args);
    }

    private void rm(String fileName) {
        String[] args = new String[2];
        args[0] = "rm";
        args[1] = fileName;
        Main.main(args);
    }

    private void status() {
        String[] args = new String[1];
        args[0] = "status";
        Main.main(args);
    }

    private void log() {
        String[] args = new String[1];
        args[0] = "log";
        Main.main(args);
    }

    private void checkoutV1(String fileName) {
        String[] args = new String[3];
        args[0] = "checkout";
        args[1] = "--";
        args[2] = fileName;
        Main.main(args);
    }

    private void checkoutV3(String branchName) {
        String[] args = new String[2];
        args[0] = "checkout";
        args[1] = branchName;
        Main.main(args);
    }

    private void merge(String branchName) {
        String[] args = new String[2];
        args[0] = "merge";
        args[1] = branchName;
        Main.main(args);
    }

    private void reset(String commitID) {
        String[] args = new String[2];
        args[0] = "reset";
        args[1] = commitID;
        Main.main(args);
    }
}
