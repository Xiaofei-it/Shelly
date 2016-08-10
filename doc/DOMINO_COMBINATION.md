# Domino Combination



#Merging and combination of Dominoes

Dominoes can be merged. If we merge two Dominoes whose final data are of the same type, then we get
a new Domino whose initial data is the union of their final data.

```
        Shelly.<String>createDomino("Find *.jpg")
                .background()
                .map(new Function1<String, File>() {
                    @Override
                    public File call(String input) {
                        return new File(input);
                    }
                })
                .flatMap(new Function1<File, List<Bitmap>>() {
                    @Override
                    public List<Bitmap> call(File input) {
                        // Find *.jpg in this folder
                        return null;
                    }
                })
                .commit();
        Shelly.<String>createDomino("Find *.png")
                .background()
                .map(new Function1<String, File>() {
                    @Override
                    public File call(String input) {
                        return new File(input);
                    }
                })
                .flatMap(new Function1<File, List<Bitmap>>() {
                    @Override
                    public List<Bitmap> call(File input) {
                        // Find *.png in this folder
                        return null;
                    }
                })
                .commit();
        Shelly.<String>createDomino("Find *.png and *.jpg")
                .background()
                .merge(Shelly.<String, Bitmap>getDominoByLabel("Find *.png"),
                        Shelly.<String, Bitmap>getDominoByLabel("Find *.jpg"))
                .uiThread()
                .target(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap input) {

                    }
                })
                .commit();
```

Also, you can write the following using anonymous Dominoes.

```
        Shelly.<String>createDomino("Find *.png and *.jpg")
                .background()
                .merge(Shelly.<String>createDomino()
                                .background()
                                .map(new Function1<String, File>() {
                                    @Override
                                    public File call(String input) {
                                        return new File(input);
                                    }
                                })
                                .flatMap(new Function1<File, List<Bitmap>>() {
                                    @Override
                                    public List<Bitmap> call(File input) {
                                        // Find *.jpg in this folder
                                        return null;
                                    }
                                }),
                        Shelly.<String>createDomino()
                                .background()
                                .map(new Function1<String, File>() {
                                    @Override
                                    public File call(String input) {
                                        return new File(input);
                                    }
                                })
                                .flatMap(new Function1<File, List<Bitmap>>() {
                                    @Override
                                    public List<Bitmap> call(File input) {
                                        // Find *.png in this folder
                                        return null;
                                    }
                                })
                )
                .uiThread()
                .target(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap input) {

                    }
                })
                .commit();
```

You can combine two Dominoes into one. Specifically, suppose there are two Dominoes named "Domino A"
and "Domino B", and you can provide a function and combine these two Dominoes into a new Domino
named "Domino C" in the following way: The final data of the two Dominoes are passed into th
function and the function returns new data as the input of Domino C.

The following is an example.

```
        Shelly.<String>createDomino("Login")
                .combine(
                        Shelly.<String>createDomino()
                                .beginRetrofitTask(new RetrofitTask<String, User>() {
                                    @Override
                                    protected Call<User> getCall(String s) {
                                        return network.getUser(s);
                                    }
                                })
                                .endTask(),
                        Shelly.<String>createDomino()
                                .beginRetrofitTask(new RetrofitTask<String, Summary>() {
                                    @Override
                                    protected Call<Summary> getCall(String s) {
                                        return network.getSummary(s);
                                    }
                                })
                                .endTask(),
                        new Function2<User, Summary, Internal>() {
                            @Override
                            public Internal call(User input1, Summary input2) {
                                return null;
                            }
                        }
                )
                .target(new Action1<Internal>() {
                    @Override
                    public void call(Internal input) {

                    }
                })
                .commit();
```

###Domino invocation within a Domino

The Shelly library provides the methods for invoking Dominoes.

The following is an example.

```
        Shelly.<String>createDomino("Login")
                .beginRetrofitTask(new RetrofitTask<String, User>() {
                    @Override
                    protected Call<User> getCall(String s) {
                        return network.getUser(s);
                    }
                })
                .onSuccessResult(
                        Shelly.<User>createDomino()
                                .beginRetrofitTask(new RetrofitTask<User, Summary>() {
                                    @Override
                                    protected Call<Summary> getCall(User user) {
                                        return network.getSummary(user.getId());
                                    }
                                })
                                .onSuccessResult(new Action1<Summary>() {
                                    @Override
                                    public void call(Summary input) {

                                    }
                                })
                                .endTask()
                )
                .endTask()
                .commit();
```
