##The machine learning algorithm and model that I used was the Polynomial interpolation with ridge regression.

`model = make_pipeline(PolynomialFeatures(2), Ridge())`
This line creates a model based on a polynomial of degree 2. Using this make_pipeline() function, we can do non-linear regression with a linear model, by adding non-linear features.