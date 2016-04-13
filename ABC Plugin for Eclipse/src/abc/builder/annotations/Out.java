package abc.builder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
/**
 * The formal parameter annotated with {@code @Out} mustn't be defined in the caller,
 * and it's the responsibility of the method to define it. If allowNull is true, the parameter
 * can be set to null in the body of the method.
 */
public @interface Out
{
	boolean allowNull() default false;
}
