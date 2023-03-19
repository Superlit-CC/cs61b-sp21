package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                MainFunc.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                MainFunc.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                MainFunc.commit(args[1]);
                break;
            case "rm":
                MainFunc.rm(args[1]);
                break;
            case "log":
                MainFunc.log();
                break;
            case "global-log":
                MainFunc.global_log();
                break;
            case "find":
                MainFunc.find(args[1]);
                break;
            case "status":
                MainFunc.status();
                break;
            case "checkout":
                if (args[1].equals("--")) {
                    MainFunc.checkoutV1(args[2]);
                } else if (args.length == 3) {
                    MainFunc.checkoutV2(args[1], args[3]);
                } else {
                    MainFunc.checkoutV3(args[1]);
                }
                break;
        }
    }
}
