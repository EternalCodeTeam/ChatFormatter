package com.eternalcode.formatter;

import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.rank.ChatRankProvider;
import com.eternalcode.formatter.template.TemplateService;

public interface ChatFormatterApi {

    PlaceholderRegistry getPlaceholderRegistry();

    TemplateService getTemplateService();

    ChatRankProvider getRankProvider();

    ChatHandler getChatHandler();

}
