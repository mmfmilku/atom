package org.mmfmilku.atom.agent.compiler.parser.handle;

public enum HandleScope {

    OUT_CLASS("类外部", 0b0001),
    IN_CLASS("类内部", 0b0010),
    IN_METHOD("方法内部", 0b0100),
    IN_CODE_BLOCK("代码块内部", 0b1000),
    ;

    HandleScope(String desc, int dealMask) {
        this.dealMask = dealMask;
    }

    private final int dealMask;

    public int getDealMask() {
        return dealMask;
    }

    public static int assembly(HandleScope ...scopes) {
        int assembled = 0;
        for (HandleScope scope : scopes) {
            assembled |= scope.getDealMask();
        }
        return assembled;
    }
}
