#ifndef IMAGES_H
#define IMAGES_H

#include "color.h"
#include "coordinate.h"
#include "functional.h"
#include <cmath>
#include <functional>

/**
 * Some useful type definitions used in further part of this project.
 */
using Fraction = double;

template <typename T>
using Base_image = std::function<T(const Point)>;

using Region = Base_image<bool>;
using Image = Base_image<Color>;
using Blend = Base_image<Fraction>;


namespace Detail {
/**
 * Auxiliary function that can be passed to lift function.
 */
auto blending = [](Color color1, Color color2, Fraction f) {
  return color1.weighted_mean(color2, f);
};

/**
 * Function emulates conditional formula.
 * Function thanks to which lift use is possible.
 */
auto conditional = [](auto if_p, auto then_p, auto else_p) {
  return if_p ? then_p : else_p;
};

/**
 * Function converts point coordinates to cartesian if necessary.
 */
auto to_cartesian = [](const Point &point) {
  return point.is_polar ? from_polar(point) : point;
};
}

/**
 * Function creates const base image.
 */
template <typename T>
Base_image<T> constant(const T t) {
  return [=](const Point &) { return t; };
}

/**
 * Function used to rotate an image by phi angle.
 * Only cartesian coordinates are passed to the function (according to moodle).
 */
template <typename T>
Base_image<T> rotate(Base_image<T> image, double phi) {
  auto rotation = [phi](const Point &point) {
    return Point(point.first * std::cos(phi) + point.second * std::sin(phi),
                 -point.first * std::sin(phi) + point.second * std::cos(phi));
  };

  return compose(rotation, image);
}

/**
 * Image translation by vector.
 * Only cartesian coordinates are passed to the function (according to moodle).
 */
template <typename T>
Base_image<T> translate(Base_image<T> image, Vector v) {
  auto translation = [v](const Point &point) {
    return Point(point.first - v.first, point.second - v.second);
  };

  return compose(translation, image);
}

/**
 * Function used to scale an image.
 * Only cartesian coordinates are passed to the function (according to moodle).
 */
template <typename T>
Base_image<T> scale(Base_image<T> image, double s) {
  auto scaling = [s](const Point &point) {
    return Point(point.first / s, point.second / s);
  };

  return compose(scaling, image);
}

/**
 * Function creates base image of circle with q as a center.
 */
template <typename T>
Base_image<T> circle(Point q, double r, T inner, T outer) {
  return [=](const Point &point) {
    return distance(Detail::to_cartesian(point), Detail::to_cartesian(q)) <= r
               ? inner
               : outer;
  };
}

/**
 * Function creates base image of checker where d is its width.
 */
template <typename T>
Base_image<T> checker(double d, T this_way, T that_way) {
  return [=](const Point &point) {
    return static_cast<int32_t>(std::floor(point.first / d) +
                                std::floor(point.second / d)) % 2
               ? that_way
               : this_way;
  };
}

/**
 * Function creates base image of polar checker.
 */
template <typename T>
Base_image<T> polar_checker(double d, int n, T this_way, T that_way) {
  auto sc = [d, n](const Point &point) {
    return Point(point.first,
                 point.second * d * static_cast<double>(n) / M_PI / 2);
  };
  return [=](const Point &point) {
    return compose(to_polar, sc, checker(d, this_way, that_way))(point);
  };
}

/**
 * Function creates base image of rings with its center in q and d as a width.
 */
template <typename T>
Base_image<T> rings(Point q, double d, T this_way, T that_way) {
  return [=](const Point &point) {
    return static_cast<int32_t>(std::floor(
               distance(Detail::to_cartesian(q), Detail::to_cartesian(point)) /
               d)) % 2
               ? that_way
               : this_way;
  };
}

/**
 * Function creates vertical stripe in the center with d as width.
 */
template <typename T>
Base_image<T> vertical_stripe(double d, T this_way, T that_way) {
  return [=](const Point &point) {
    return std::abs(point.first) * 2 <= d ? this_way : that_way;
  };
}

/**
 * Function cuts out regions of images depending on region value.
 */
inline Image cond(Region region, Image this_way, Image that_way) {
  return [=](const Point &point) {
    return lift(Detail::conditional, region, this_way, that_way)(point);
  };
}

/**
 * Function blends colors of two images with certain weight and
 * creates new color.
 */
inline Image lerp(Blend blend, Image this_way, Image that_way) {
  return [=](const Point &point) {
    return lift(Detail::blending, this_way, that_way, blend)(point);
  };
}

/**
 * Function uses lerp to darken image.
 */
inline Image darken(Image image, Blend blend) {
  return lerp(blend, image, constant(Colors::black));
}

/**
 * Function uses lerp to lighten image.
 */
inline Image lighten(Image image, Blend blend) {
  return lerp(blend, image, constant(Colors::white));
}

#endif // IMAGES_H
