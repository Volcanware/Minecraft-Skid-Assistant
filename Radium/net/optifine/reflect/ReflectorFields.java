// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.reflect;

public class ReflectorFields
{
    private ReflectorClass reflectorClass;
    private Class fieldType;
    private int fieldCount;
    private ReflectorField[] reflectorFields;
    
    public ReflectorFields(final ReflectorClass reflectorClass, final Class fieldType, final int fieldCount) {
        this.reflectorClass = reflectorClass;
        this.fieldType = fieldType;
        if (reflectorClass.exists() && fieldType != null) {
            this.reflectorFields = new ReflectorField[fieldCount];
            for (int i = 0; i < this.reflectorFields.length; ++i) {
                this.reflectorFields[i] = new ReflectorField(reflectorClass, fieldType, i);
            }
        }
    }
    
    public ReflectorClass getReflectorClass() {
        return this.reflectorClass;
    }
    
    public Class getFieldType() {
        return this.fieldType;
    }
    
    public int getFieldCount() {
        return this.fieldCount;
    }
    
    public ReflectorField getReflectorField(final int index) {
        return (index >= 0 && index < this.reflectorFields.length) ? this.reflectorFields[index] : null;
    }
}
