package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0 || args == null) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                MainFunc.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.checkNumberOfArgs(2, args.length);
                Repository.checkGitlet();
                MainFunc.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length == 1 || args[1].isEmpty()) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.checkGitlet();
                MainFunc.commit(args[1]);
                break;
            case "rm":
                Repository.checkNumberOfArgs(2, args.length);
                Repository.checkGitlet();
                MainFunc.rm(args[1]);
                break;
            case "log":
                Repository.checkGitlet();
                MainFunc.log();
                break;
            case "global-log":
                Repository.checkGitlet();
                MainFunc.global_log();
                break;
            case "find":
                Repository.checkNumberOfArgs(2, args.length);
                Repository.checkGitlet();
                MainFunc.find(args[1]);
                break;
            case "status":
                Repository.checkGitlet();
                MainFunc.status();
                break;
            case "checkout":
                Repository.checkGitlet();
                if (args.length == 1 || args.length > 4) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                } else if (args.length == 3) {  // 如果参数数量是3，就认为是v1
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    MainFunc.checkoutV1(args[2]);
                } else if (args.length == 4) {  // 如果参数数量是4，就认为是v2
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    MainFunc.checkoutV2(args[1], args[3]);
                } else {  // 否则是v3
                    MainFunc.checkoutV3(args[1]);
                }
                break;
            case "branch":
                Repository.checkNumberOfArgs(2, args.length);
                Repository.checkGitlet();
                MainFunc.branch(args[1]);
                break;
            case "rm-branch":
                Repository.checkNumberOfArgs(2, args.length);
                Repository.checkGitlet();
                MainFunc.rmBranch(args[1]);
                break;
            case "reset":
                Repository.checkNumberOfArgs(2, args.length);
                Repository.checkGitlet();
                MainFunc.reset(args[1]);
                break;
            case "merge":
                Repository.checkNumberOfArgs(2, args.length);
                Repository.checkGitlet();
                MainFunc.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
                break;
        }
    }
}
