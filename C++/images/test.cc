#include "functional.h"
#include "images.h"
#include <cassert>
#include <functional>
#include <iostream>

int main() {

  int x = 10;
  assert(compose()(x) == 10);

  assert(compose()(42) == 42);
  assert(compose([](auto x) { return x + 1; })(1) == 2);
  assert(compose([](auto x) { return x + 1; },
                 [](auto x) { return x * x; },
                 [](auto x) { return x + 3; })(1) == 7);


  const auto h1 = [](auto x) { return 5; };
  assert(lift(h1)(10) == 5) ;

}