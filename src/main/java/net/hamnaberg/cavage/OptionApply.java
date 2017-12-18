package net.hamnaberg.cavage;

import io.vavr.*;
import io.vavr.control.Option;

public final class OptionApply {
    private OptionApply() {
    }

    public static <A, T1, T2> Option<A> apply(Option<T1> t1, Option<T2> t2, Function2<T1, T2, A> f) {
        return t1.flatMap(v1 -> t2.map(v2 -> f.apply(v1, v2)));
    }

    public static <A, T1, T2, T3> Option<A> apply(Option<T1> t1, Option<T2> t2, Option<T3> t3, Function3<T1, T2, T3, A> f) {
        return t1.flatMap(v1 -> t2.flatMap(v2 -> t3.map(v3 -> f.apply(v1, v2, v3))));
    }

    public static <A, T1, T2, T3, T4> Option<A> apply(Option<T1> t1, Option<T2> t2, Option<T3> t3, Option<T4> t4, Function4<T1, T2, T3, T4, A> f) {
        return  t1.flatMap(v1 ->
                t2.flatMap(v2 ->
                t3.flatMap(v3 ->
                t4.map( v4 -> f.apply(v1, v2, v3, v4)))));
    }

    public static <A, T1, T2, T3, T4, T5> Option<A> apply(Option<T1> t1, Option<T2> t2, Option<T3> t3, Option<T4> t4, Option<T5> t5, Function5<T1, T2, T3, T4, T5, A> f) {
        return  t1.flatMap(v1 ->
                t2.flatMap(v2 ->
                t3.flatMap(v3 ->
                t4.flatMap(v4 ->
                t5.map( v5 -> f.apply(v1, v2, v3, v4, v5))))));
    }

    public static <A, T1, T2, T3, T4, T5, T6> Option<A> apply(Option<T1> t1, Option<T2> t2, Option<T3> t3, Option<T4> t4, Option<T5> t5, Option<T6> t6, Function6<T1, T2, T3, T4, T5, T6, A> f) {
        return  t1.flatMap(v1 ->
                t2.flatMap(v2 ->
                t3.flatMap(v3 ->
                t4.flatMap(v4 ->
                t5.flatMap(v5 ->
                t6.map( v6 -> f.apply(v1, v2, v3, v4, v5, v6)))))));
    }

    public static <A, T1, T2, T3, T4, T5, T6, T7> Option<A> apply(Option<T1> t1, Option<T2> t2, Option<T3> t3, Option<T4> t4, Option<T5> t5, Option<T6> t6, Option<T7> t7, Function7<T1, T2, T3, T4, T5, T6, T7, A> f) {
        return  t1.flatMap(v1 ->
                t2.flatMap(v2 ->
                t3.flatMap(v3 ->
                t4.flatMap(v4 ->
                t5.flatMap(v5 ->
                t6.flatMap(v6 ->
                t7.map( v7 -> f.apply(v1, v2, v3, v4, v5, v6, v7))))))));
    }

    public static <A, T1, T2, T3, T4, T5, T6, T7, T8> Option<A> apply(Option<T1> t1, Option<T2> t2, Option<T3> t3, Option<T4> t4, Option<T5> t5, Option<T6> t6, Option<T7> t7, Option<T8> t8, Function8<T1, T2, T3, T4, T5, T6, T7, T8, A> f) {
        return  t1.flatMap(v1 ->
                t2.flatMap(v2 ->
                t3.flatMap(v3 ->
                t4.flatMap(v4 ->
                t5.flatMap(v5 ->
                t6.flatMap(v6 ->
                t7.flatMap(v7 ->
                t8.map(v8 -> f.apply(v1, v2, v3, v4, v5, v6, v7, v8)))))))));
    }
}
