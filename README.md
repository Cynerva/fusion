# fusion

State propagation via atoms. Usable in Clojure and ClojureScript.

## Dependency
```
[twinfoxcreations/fusion "0.1.0-SNAPSHOT"]
```

## Example

```
(require '[fusion.core :refer [fuse]])

(def a (atom 1))
(def b (atom 2))
(def sum (fuse (+ @a @b)))

@sum ; 3

(reset! a 4)
(reset! b 5)

@sum ; 9
```

In this example, `sum` is a `FusedAtom` which, when dereferenced, returns the
sum of `a` and `b`.

Fused atoms are _lazy_: the body is not evaluated until it is dereferenced.
Once evaluated, the value is stored for later.

Atoms dereferenced in the body are automatically watched for changes - if `a`
or `b` change, then `sum` is dirtied so it will recompute its value the next
time it is dereferenced.

## Purpose

For some problems, it can be nice to model state using a chain of connected
atoms. Events that trigger updates in a source atom can automatically propagate
through any dependent (fused) atoms.

Similar to ideas from Functional-Reactive Programming, though this library
doesn't address the event side of things. State propagation only.

Actually, this is really just an excuse to practice test-driven development and
library deployment. Hurray!
