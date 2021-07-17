package de.uwuwhatsthis.supportBotForDev.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Args {
    // custom args class, used for command args

    private final List<String> args;

    public Args(String message){
        List<String> args = Arrays.asList(message.split(" "));
        List<String> parsedArgs = new ArrayList<String>(){{
            for (String string: args){
                if (args.indexOf(string) == 0){
                    // the command name shouldn't be parsed as an argument
                    continue;
                }

                if (string.length() == 0){
                    // string is empty
                    continue;
                }
                add(string);
            }
        }};

        this.args = new ArrayList<String>(){{
            String multiSpaceArgs = "";
            for (String argument: parsedArgs){
                if ((argument.startsWith("\"") || argument.startsWith("'")) && multiSpaceArgs.length() == 0){
                    argument = argument.replace("\"", "").replace("'", "");
                    multiSpaceArgs += argument;
                    continue;
                }

                if ((argument.endsWith("\"") || argument.endsWith("'")) && multiSpaceArgs.length() > 0){
                    argument = argument.replace("\"", "").replace("'", "");
                    multiSpaceArgs += " " + argument;
                    add(multiSpaceArgs);
                    multiSpaceArgs = "";
                    continue;
                }

                if (multiSpaceArgs.length() > 0){
                    multiSpaceArgs += " " + argument;
                    continue;
                }

                add(argument);
            }
            if (multiSpaceArgs.length() > 0){
                // in case of faulty args, add them
                if (multiSpaceArgs.startsWith(" ")) multiSpaceArgs = multiSpaceArgs.replaceFirst(" ", "");
                add(multiSpaceArgs);
            }
        }};
    }

    public Boolean isEmpty(){
        return args.size() == 0;
    }

    public List<String> getArgs() {
        return args;
    }

    public String get(int index){return args.get(index);}

    public Integer size(){return args.size();}
}
