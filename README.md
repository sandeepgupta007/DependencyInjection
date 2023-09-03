# DependencyInjection

Design dependency injection framework.

Here, we use dependency container to register the classes and there type which we would later request dependency container to resolve the Objects for it which it will lazily do.

We use reflection currently to intiantiate the objects dynamically.
