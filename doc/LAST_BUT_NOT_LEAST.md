
###Tuple input

You may find that in the above examples, all of the Dominoes take only one input each time, but how
about multiple input?

The Shelly library provides you with some tuple classes, in which you can put multiple inputs.

The following is an example.

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

###Stash

Sometimes, in an action performed by a Domino, you want to save something for later use. Now you can
use the stash actions or stash functions, whose classes are located in the package
`xiaofei.library.shelly.function.stashfunction`. These actions and functions are the same as their
corresponding actions and functions except that they provide additional methods for stashing, with
which you can stash data for later use.

The following is an example:

```
```