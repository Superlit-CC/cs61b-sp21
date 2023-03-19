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
        if (args == null) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.checkNumberOfArgs(1, args.length);
                MainFunc.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.checkNumberOfArgs(2, args.length);
                MainFunc.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.checkNumberOfArgs(2, args.length);
                MainFunc.commit(args[1]);
                break;
            case "rm":
                Repository.checkNumberOfArgs(2, args.length);
                MainFunc.rm(args[1]);
                break;
            case "log":
                Repository.checkNumberOfArgs(1, args.length);
                MainFunc.log();
                break;
            case "global-log":
                Repository.checkNumberOfArgs(1, args.length);
                MainFunc.global_log();
                break;
            case "find":
                Repository.checkNumberOfArgs(2, args.length);
                MainFunc.find(args[1]);
                break;
            case "status":
                Repository.checkNumberOfArgs(1, args.length);
                MainFunc.status();
                break;
            case "checkout":
                if (args.length == 1) {
                    Repository.checkNumberOfArgs(2, args.length);
                }
                if (args[1].equals("--")) {
                    Repository.checkNumberOfArgs(3, args.length);
                    MainFunc.checkoutV1(args[2]);
                } else if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    MainFunc.checkoutV2(args[1], args[3]);
                } else {
                    Repository.checkNumberOfArgs(2, args.length);
                    MainFunc.checkoutV3(args[1]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
                break;
        }
    }
}
