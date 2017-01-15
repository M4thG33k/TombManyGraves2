package com.m4thg33k.tombmanygraves.api.state;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;
import java.util.Objects;

public class PropertyObject<T> implements IUnlistedProperty<T> {

    private final String name;
    private final Class<T> tClass;
    private final Predicate<T> validator;
    private final Function<T, String> stringFunction;

    public PropertyObject(String name, Class<T> tClass, Predicate<T> validator, Function<T, String> stringFunction)
    {
        this.name = name;
        this.tClass = tClass;
        this.validator = validator;
        this.stringFunction = stringFunction;
    }

    public PropertyObject(String name, Class<T> tClass)
    {
        this(name, tClass, Predicates.<T>alwaysTrue(), new Function<T, String>() {
            @Nullable
            @Override
            public String apply(@Nullable T input) {
                return Objects.toString(input);
            }
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getType() {
        return tClass;
    }

    @Override
    public boolean isValid(T value) {
        return validator.apply(value);
    }

    @Override
    public String valueToString(T value) {
        return stringFunction.apply(value);
    }
}
