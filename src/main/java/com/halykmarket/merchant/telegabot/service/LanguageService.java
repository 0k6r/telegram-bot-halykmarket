package com.halykmarket.merchant.telegabot.service;

import com.halykmarket.merchant.telegabot.enums.Language;
import com.halykmarket.merchant.telegabot.exceptions.KeyboardNotFoundException;
import com.halykmarket.merchant.telegabot.model.standart.LanguageUser;
import com.halykmarket.merchant.telegabot.repository.LanguageUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LanguageService {
    private static Map<Long, Language> languageMap = new HashMap<>();
    private final LanguageUserRepo languageUserRepo;

    public Language getLanguage(long chatId) {
        Language language = languageMap.get(chatId);
        if (language == null) {
            LanguageUser languageUser = languageUserRepo.findByChatId(chatId)
                    .orElseThrow(() -> new KeyboardNotFoundException("Keyboard with charId: " + chatId + "not found"));
            if (languageUser != null) {
                language = Language.getById(languageUser.getLanguageId());
                languageMap.put(chatId, language);
            }
        }
        return language;
    }

    public void setLanguage(long chatId, Language language) {
        languageMap.put(chatId, language);
        LanguageUser languageUser = languageUserRepo.findByChatId(chatId)
                .orElseThrow(() -> new KeyboardNotFoundException("Keyboard with charId: " + chatId + "not found"));
        if (languageUser == null) {
            languageUserRepo.save(new LanguageUser().setChatId(chatId).setLanguageId(language.getId()));
        } else {
            languageUserRepo.save(languageUser.setLanguageId(language.getId()));
        }
    }
}
