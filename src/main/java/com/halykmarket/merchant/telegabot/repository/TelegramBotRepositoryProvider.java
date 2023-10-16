package com.halykmarket.merchant.telegabot.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotRepositoryProvider {

    @Getter
    @Setter
    private static PropertiesRepo propertiesRepo;
    @Getter
    @Setter
    private static LanguageUserRepo languageUserRepo;
    @Getter
    @Setter
    private static UsersRepo usersRepo;
    @Getter
    @Setter
    private static ButtonRepo buttonRepo;
    @Getter
    @Setter
    private static MessageRepo messageRepo;
    @Getter
    @Setter
    private static KeyboardRepo keyboardRepo;
    @Getter
    @Setter
    private static AdminRepo adminRepo;
    @Getter
    @Setter
    private static ComeTimeRepo comeTimeRepo;
    @Getter
    @Setter
    private static OutTimeRepo  outTimeRepo;
//    @Getter
//    @Setter
//    private static ReminderRepo reminderRepo;


    //---------------------------------------------------------------
    @Autowired
    public TelegramBotRepositoryProvider(
                                         PropertiesRepo propertiesRepo, LanguageUserRepo languageUserRepo,
                                         UsersRepo usersRepo, ButtonRepo buttonRepo, MessageRepo messageRepo,
                                         KeyboardRepo keyboardRepo, AdminRepo adminRepo, ComeTimeRepo comeTimeRepo, OutTimeRepo outTimeRepo

                                      //   ReminderRepo reminderRepo
    ) {
        setPropertiesRepo(propertiesRepo);
        setLanguageUserRepo(languageUserRepo);
        setUsersRepo(usersRepo);
        setButtonRepo(buttonRepo);
        setMessageRepo(messageRepo);
        setKeyboardRepo(keyboardRepo);
        setAdminRepo(adminRepo);
        setComeTimeRepo(comeTimeRepo);
        setOutTimeRepo(outTimeRepo);
       // setReminderRepo(reminderRepo);

    }
}
