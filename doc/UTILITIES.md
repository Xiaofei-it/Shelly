# Utilities

## Tuple input

You may find that in the previous examples, the type of the input of all the Dominoes is a single type.
You can pass an integer to the Domino, or a double.

See another example:

```
Shelly.<Integer>createDomino("Print")
        .target(new Action1<Integer>() {
            @Override
            public void call(Integer input) {
                System.out.println(input);
            }
        })
        .commit();
```

If you invoke the Domino by `Shelly.playDomino("Print", 3)`, the Shelly library prints "3".

If you invoke the Domino by `Shelly.playDomino("Print", 3, 5)`, the Shelly library prints "3" and "5".

What should you do if you want to pass two integer and a double to the Domino at the same time?
For example, if you want to create a Domino which take two integers and a double as input and
print the sum of the three numbers.

The Shelly library provides you with some tuple classes, in which you can put multiple objects.

To print the sum of two integers and a double, you write the following to create the Domino:

```
Shelly.<Triple<Integer, Integer, Double>>createDomino("Add")
        .map(new Function1<Triple<Integer,Integer,Double>, Double>() {
            @Override
            public Double call(Triple<Integer, Integer, Double> input) {
                return input.first + input.second + input.third;
            }
        })
        .target(new Action1<Double>() {
            @Override
            public void call(Double input) {
                System.out.print(input);
            }
        })
        .commit();
```

Invoke the Domino with the following statement:

```
Shelly.playDomino("Add", Triple.create(1, 2, 3.0));
```

The Shelly library provides you with many tuple classes, which are contained in the package
`xiaofei.library.shelly.tuple`. You can create a tuple by `XXX.create(...)` rather than using the
constructor of a tuple class.

###Stash

Sometimes, in an action performed by a Domino, you want to save something for later use. Now you can
use the stash actions or stash functions, whose classes are located in the package
`xiaofei.library.shelly.function.stashfunction`. These actions and functions are the same as their
corresponding actions and functions except that they provide additional methods for stashing, with
which you can stash data for later use.

The following is an example:

```
```