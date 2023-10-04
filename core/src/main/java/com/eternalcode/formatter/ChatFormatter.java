package com.eternalcode.formatter;

import com.eternalcode.formatter.template.TemplateService;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.preparatory.ChatPreparatoryService;

public interface ChatFormatter {

    PlaceholderRegistry getPlaceholderRegistry();

    TemplateService getTemplateService();

    ChatRankProvider getRankProvider();

    ChatPreparatoryService getChatPreparatoryService();

}
