package com.halykmarket.merchant.telegabot.command;

import com.halykmarket.merchant.telegabot.command.impl.*;
import com.halykmarket.merchant.telegabot.exceptions.NotRealizedMethodException;

public class CommandFactory {
    public static Command getCommand(long id) {
        Command result = getCommandWithoutReflection((int) id);
        if (result == null) throw new NotRealizedMethodException("Not realized for type: " + id);
        return result;
    }
    private static Command getCommandWithoutReflection(int id) {
        return switch (id) {
            case 1 -> new id001_ShowInfo();
            case 2 -> new id002_Registration();
            case 3 -> new id003_SelectionLanguage();
            case 4 -> new id004_FileOrPhoto();
            case 5 -> new id005_ShowAdminInfo();
            case 6 -> new id006_EditAdmin();
            default -> null;
        };
    }
}
