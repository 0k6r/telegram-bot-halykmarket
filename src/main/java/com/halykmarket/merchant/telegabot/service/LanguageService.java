package com.halykmarket.merchant.telegabot.service;

import com.halykmarket.merchant.telegabot.repository.LanguageUserRepo;
import com.halykmarket.merchant.telegabot.repository.UsersRepo;
import org.springframework.stereotype.Component;
import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.model.standart.LanguageUser;
import com.halykmarket.merchant.telegabot.repository.TelegramBotRepositoryProvider;

import java.util.HashMap;
import java.util.Map;

@Component
public class LanguageService {

    private static  Map<Long, Language>     languageMap         = new HashMap<>();
    private static LanguageUserRepo languageUserRepo    = TelegramBotRepositoryProvider.getLanguageUserRepo();
    private static UsersRepo usersRepo           = TelegramBotRepositoryProvider.getUsersRepo();

    public  static  Language    getLanguage(long chatId) {
        Language language = languageMap.get(chatId);
        if (language == null) {
            LanguageUser languageUser = languageUserRepo.getByChatId(chatId);
            if (languageUser != null) {
                language = Language.getById(languageUser.getLanguageId());
                languageMap.put(chatId, language);
            }
        }
        return language;
    }

    public  static  void        setLanguage(long chatId, Language language) {
        languageMap.put(chatId, language);
        LanguageUser languageUser = languageUserRepo.getByChatId(chatId);
        if (languageUser == null) {
            languageUserRepo.save(new LanguageUser().setChatId(chatId).setLanguageId(language.getId()));
        } else {
            languageUserRepo.save(languageUser.setLanguageId(language.getId()));
        }
    }
}
