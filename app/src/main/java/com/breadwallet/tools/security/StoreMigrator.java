package com.breadwallet.tools.security;

import java.util.Map;

public class StoreMigrator {
    public static final String PASS_CODE_ALIAS = "passCode";
    private static final String PASS_CODE_FILENAME = "my_pass_code";
    private static final String PASS_CODE_IV = "ivpasscode";
    public static final Map<String, BRKeyStore.AliasObject> ALIAS_OBJECT_MAP = new

    public StoreMigrator(){
        ALIAS_OBJECT_MAP.put(PASS_CODE_ALIAS, new BRKeyStore.AliasObject(PASS_CODE_ALIAS, PASS_CODE_FILENAME, PASS_CODE_IV));
    }
}

