// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.expr;

public class FunctionFloatArray implements IExpressionFloatArray
{
    private FunctionType type;
    private IExpression[] arguments;
    
    public FunctionFloatArray(final FunctionType type, final IExpression[] arguments) {
        this.type = type;
        this.arguments = arguments;
    }
    
    @Override
    public float[] eval() {
        return this.type.evalFloatArray(this.arguments);
    }
    
    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.FLOAT_ARRAY;
    }
    
    @Override
    public String toString() {
        return this.type + "()";
    }
}
