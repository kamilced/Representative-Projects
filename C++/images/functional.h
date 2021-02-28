#ifndef FUNCTIONAL_H
#define FUNCTIONAL_H

/**
 * Function composes variadic list of functions.
 */
template <typename... Fs>
inline auto compose() ;

template <>
inline auto compose() {
  return [](auto x) { return x; };
}

template <typename F, typename G>
inline auto compose(F f, G g) {
  return [f, g](auto x) { return g(f(x)); };
}

template <typename F, typename... Fs>
inline auto compose(F f, Fs... fs) {
  return compose(f, compose(fs...));
}

/**
 * Function used for pointwise lifting realization.
 */
template <typename H>
inline auto lift(H h) {
  return [h](auto x) { return h(x); };
}

template <typename H, typename... Fs>
inline auto lift(H h, Fs... fs) {
  return [h, fs...](auto x) { return h(fs(x)...); };
}

#endif // FUNCTIONAL_H
