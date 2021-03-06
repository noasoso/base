package org.forkjoin.apikit.info;

import com.google.common.collect.ImmutableMap;
import org.forkjoin.apikit.AnalyseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类型信息
 *
 * @author zuoge85 on 15/6/12.
 */
public class TypeInfo implements Cloneable{
    private static final Logger log = LoggerFactory.getLogger(TypeInfo.class);

    private Type type;
    private String packageName;
    private String name;
    private boolean isArray;
    private List<TypeInfo> typeArguments = new ArrayList<>();
    private boolean isInside = false;
    /**
     * 是否是泛型
     */
    private boolean isGeneric = false;

    private TypeInfo() {
    }

    public void addArguments(TypeInfo typeInfo) {
        typeArguments.add(typeInfo);
    }

    public static TypeInfo formGeneric(String name, boolean isArray) {
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.type = Type.OTHER;
        typeInfo.isArray = isArray;
        typeInfo.isInside = false;
        typeInfo.isGeneric = true;
        typeInfo.name = name;
        return typeInfo;
    }

    public static TypeInfo formBaseType(String name, boolean isArray) {
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.type = TypeInfo.Type.form(name);
        typeInfo.isArray = isArray;
        typeInfo.isInside = false;

        if (!typeInfo.type.isBaseType()) {
            throw new AnalyseException("错误的类型,不是base类型:" + name);
        }
        return typeInfo;
    }

    public void setArray(boolean array) {
        this.isArray = array;
    }

    public List<TypeInfo> getTypeArguments() {
        return typeArguments;
    }

    public static TypeInfo formImport(Import typeName, boolean isArray) {
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.type = TypeInfo.Type.form(typeName.getFullName());
        typeInfo.isArray = isArray;
        if (!typeInfo.type.isBaseType()) {
            typeInfo.packageName = typeName.getPackageName();
            typeInfo.name = typeName.getName();
            typeInfo.isInside = typeName.isInside();
        }
        return typeInfo;
    }

    public Type getType() {
        return type;
    }

    public String getFullName() {
        if (packageName == null) {
            return name;
        }
        return packageName + "." + name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean isBytes() {
        return isArray() && type == Type.BYTE;
    }


    public boolean isArrayOrList() {
        return (isArray() || isList()) && type != Type.BYTE;
    }

    public boolean isList() {
        try {
            String fullName = getFullName();
            if (fullName != null) {
                Class<?> cls = Class.forName(fullName);
                return List.class.isAssignableFrom(cls);
            }
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public boolean isInside() {
        return isInside;
    }

    public void setInside(boolean inside) {
        isInside = inside;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    public void setGeneric(boolean generic) {
        isGeneric = generic;
    }


    @Override
    public TypeInfo clone()  {
        try {
            return (TypeInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "TypeInfo{" +
                "type=" + type +
                ", packageName='" + packageName + '\'' +
                ", name='" + name + '\'' +
                ", isArray=" + isArray +
                ", typeArguments=" + typeArguments +
                '}';
    }

    public boolean isOtherType() {
        return type == Type.OTHER;
    }


    /**
     * 0. void *(只在api返回值)*
     * 1. boolean
     * 2. byte *(8位有符号整数)*
     * 3. short *(16位有符号整数)*
     * 4. int *(32位有符号整数)*
     * 5. long *(64位有符号整数)*
     * 6. float *(32位浮点数)*
     * 7. double *(64位浮点数)*
     * 8. String
     * 9. Date
     * 10. enum 枚举类型，只支持简单枚举类型
     * 11. Message类型
     * <p>
     * enum Message 和其他非上面声明类型都不属于basic type
     *
     * @author zuoge85
     */
    public enum Type {
        VOID, BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT,
        DOUBLE, STRING, DATE,
        OTHER;


        private static final ImmutableMap<String, Type> typeMap = ImmutableMap.<String, Type>builder()
                .put(void.class.getSimpleName(), VOID)
                .put(boolean.class.getSimpleName(), BOOLEAN)
                .put(byte.class.getSimpleName(), BYTE)
                .put(short.class.getSimpleName(), SHORT)
                .put(int.class.getSimpleName(), INT)
                .put(long.class.getSimpleName(), LONG)
                .put(float.class.getSimpleName(), FLOAT)
                .put(double.class.getSimpleName(), DOUBLE)

                .put(Void.class.getName(), VOID)
                .put(Boolean.class.getName(), BOOLEAN)
                .put(Byte.class.getName(), BYTE)
                .put(Short.class.getName(), SHORT)
                .put(Integer.class.getName(), INT)
                .put(Long.class.getName(), LONG)
                .put(Float.class.getName(), FLOAT)
                .put(Double.class.getName(), DOUBLE)

                .put(String.class.getName(), STRING)
                .put(Date.class.getName(), DATE)
                .build();


        public static boolean isHasNull(Type type) {
            return type == STRING || type == DATE ||
                    type == OTHER;
        }

        public boolean isHasNull() {
            return isHasNull(this);
        }

        public static boolean isBaseType(Type type) {
            return type != OTHER;
        }

        public boolean isBaseType() {
            return isBaseType(this);
        }


        public static Type form(String name) {
            Type type = typeMap.get(name);
            if (type == null) {
                return OTHER;
            }
            return type;
        }
    }
}
