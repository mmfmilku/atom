package org.mmfmilku.atom.agent.config;

/**
 * Keywords
 *
 * @author chenxp
 * @date 2024/6/19:9:22
 */
public enum Keywords {
    
    METHOD("method"), 
    PRINT("print"), 
    PRINT_PARAM("pprint"), 
    PRINT_RETURN("rprint"),
    PRINT_CALL("cprint"),
    ;

    private String keyword;

    Keywords(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    /**
     * return (keyword1|keyword2)
     */
    public static String toRegStr() {
        Keywords[] values = values();
        StringBuilder regBuilder = new StringBuilder("(");
        for (int i = 0; i < values.length; i++) {
            regBuilder.append(values[i].keyword);
            if (i != values.length - 1) {
                regBuilder.append("|");
            }
        }
        regBuilder.append(")");
        return regBuilder.toString();
    }
}
