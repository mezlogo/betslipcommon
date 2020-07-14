export namespace kotlin {
    type Long = number
    namespace collections {
        interface List<T> {
            toArray(): T[]
        }
    }
    namespace js {
        type Promise<T> = any
    }
}