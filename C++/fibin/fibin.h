#ifndef __FIBIN_H__
#define __FIBIN_H__

#include <type_traits>
#include <cstdint>
#include <iostream>

/* Stała używana przy funkcji hashującej. */
#define BASE 37
/* Maksymalna długość słowa będącego argumentem funkcji Var. */
#define MAX_LEN 6

/* Typ zwracany przez Var. */
typedef uint32_t var_t;
/* Typ przyjmowany przy obliczaniu kolejnych liczb Fibonacciego. */
typedef uint32_t var_Fib;

/* Deklaracja wartości logicznych fibina. */
typedef std::integral_constant<bool, true> True;
typedef std::integral_constant<bool, false> False;

/* Deklaracje stuktur. */
template<var_Fib N>
struct Fib;

template<typename T>
struct Lit;

template<var_t Name, typename Value, typename Expr>
struct Let;

template<var_t Name>
struct Ref;

template<var_t Name, typename Body>
struct Lambda;

template<typename Fun, typename Param>
struct Invoke;

template <typename... Args>
struct Sum;

template<typename Arg>
struct Inc1;

template<typename Arg>
struct Inc10;

template<typename Condition, typename Then, typename Else>
struct If;

template<typename Left, typename Right>
struct Eq;

/* Zapis zmiennej Var jako napisu, obsługiwanego przez program. */
static constexpr var_t Var(const char* Name) {
	size_t i = 0;
	var_t ret = 0, pow = 1;
	while(Name[i] != '\0' && i < MAX_LEN + 1) {
		if (Name[i] >= '0' && Name[i] <= '9') {
			ret += (Name[i] - '0' + 1) * pow;
		}
		else if (Name[i] >= 'a' && Name[i] <= 'z') {
			ret += (Name[i] - 'a' + 11) * pow;
		}
		else if (Name[i] >= 'A' && Name[i] <= 'Z') {
			ret += (Name[i] - 'A' + 11) * pow;
		}
		else {
			return 0;
		}
		pow *= BASE;
		i++;
	}

	if ((i == MAX_LEN + 1 && Name[MAX_LEN] != '\0') || i == 0) {
		return 0;
	}

	return ret;
}

/* Własciwa klasa Fibin. */
template<typename ValueType>
class Fibin {
private:

	/* Struktura wspomagająca wyliczanie poszczególnych wyrażeń */
  template<typename Expr, typename Env>
  struct Eval : std::false_type {};

	/* Struktura wyliczająca rekurencyjnie wartości Lit<Fib<N>>. */
  template<typename Expr, typename Env>
  struct Evaluator;

	template<typename Env>
	struct Evaluator<Fib<0>, Env> {
		static constexpr ValueType value = 0;
	};

	template<typename Env>
	struct Evaluator<Fib<1>, Env> {
		static constexpr ValueType value = 1;
	};

	template<var_Fib N, typename Env>
	struct Evaluator<Fib<N>, Env> {
		static constexpr ValueType value = Evaluator<Fib<N - 2>, Env>::value +
                                            Evaluator<Fib<N - 1>, Env>::value;
	};

	template<typename Env>
	struct Eval<Lit<Fib<0>>, Env> {
		using value = Evaluator<Fib<0>, Env>;
	};

	template<typename Env>
	struct Eval<Lit<Fib<1>>, Env> {
		using value = Evaluator<Fib<1>, Env>;
	};

	template<var_Fib N, typename Env>
	struct Eval<Lit<Fib<N>>, Env> {
		static_assert(N >= 0);
		using value = Evaluator<Fib<N>, Env>;
	};

  /* Ewaulacja literałów logicznych Lit<True> i Lit<False>. */
	template<typename Env>
	struct Eval<Lit<True>, Env> {
		using value = Lit<True>;
	};

	template<typename Env>
	struct Eval<Lit<False>, Env> {
		using value = Lit<False>;
	};

  /* Struktura pomocnicza, na której odbywa się budowa listy. */
	struct EmptyEnv;

	/* Lista przehowująca zmienne i ich wartości */
	template<var_t Name, typename Value, typename Env>
	struct List;

	/* Struktura pozwalająca na wyszukiwanie w liście wartości przypisanych do etykiet */
	template<var_t Name1, typename L>
	struct Find {};

	template<var_t Name, typename Value, typename Env>
	struct Find<Name, List<Name, Value, Env>> {
		using value = Value;
	};

	template<var_t Name>
	struct Find<Name, EmptyEnv> {};

	template<var_t Name1, var_t Name2, typename Value2, typename Env>
	struct Find<Name1, List<Name2, Value2, Env>> {
		using value = typename Find<Name1, Env>::value;
	};

    /* Implementacja odwołań Ref i wyrażeń Let. */
	template<var_t Name, typename Env>
	struct Eval<Ref<Name>, Env> {
		static_assert(Name != 0);
		using value = typename Find<Name, Env>::value::value;
	};

	template<var_t Name, typename Value, typename Expr, typename Env>
	struct Eval<Let<Name, Value, Expr>, Env> {
		static_assert(Name != 0);
		using value = typename Eval<Expr, List<Name, Eval<Value, Env>, Env>>::value;
	};

    /* Implementacja wyrażeń Lambda i ich wywołań Invoke. */
	template <typename Proc, typename Value>
	struct Apply;

	template <typename Lam, typename Env>
	struct Lambda_Tuple;

	template<var_t Name, typename Body, typename Env>
	struct Eval<Lambda<Name, Body>, Env> {
		static_assert(Name != 0);
		using value = Lambda_Tuple<Lambda<Name, Body>, Env>;
	};

	template<typename Fun, typename Param, typename Env>
	struct Eval<Invoke<Fun, Param> , Env> {
		using value = typename Apply<typename Eval<Fun, Env>::value,
                                            Eval<Param, Env>>::value::value;
	};

	template<var_t Name, typename Body, typename Env, typename Value>
	struct Apply<Lambda_Tuple<Lambda<Name,Body>, Env>, Value> {
		using value = Eval<Body, List<Name,Value,Env>>;
	};
    
    /* Implementacja wyrażeń If. */
	template<typename Then, typename Else, typename Env>
	struct Eval<If<Lit<True>, Then, Else>, Env> {
		using value = typename Eval<Then, Env>::value;
	};

	template<typename Then, typename Else, typename Env>
	struct Eval<If<Lit<False>, Then, Else>, Env> {
		using value = typename Eval<Else, Env>::value;
	};

	template<typename Condition, typename Then, typename Else, typename Env>
	struct Eval<If<Condition, Then, Else>, Env> {
		using value = typename
				Eval<If<typename Eval<Condition, Env>::value, Then, Else>, Env>::value;
	};

    /* Struktura pomocnicza, której zadaniem jest zamiana standardowych zmiennych typu bool
     * na Lit<True> lub Lit<False>, wykorzystywana przez porównanie Eq<Left, Right>. */
	template<bool B>
	struct Converter;

	template<typename Env>
	struct Eval<Converter<true>, Env> {
		using value = typename Eval<Lit<True>, Env>::value;
	};

	template<typename Env>
	struct Eval<Converter<false>, Env> {
		using value = typename Eval<Lit<False>, Env>::value;
	};

	template<typename Left, typename Right, typename Env>
	struct Eval<Eq<Left, Right>, Env> {
		using value = typename Eval<Converter<Eval<Left, Env>::value::value ==
                                Eval<Right, Env>::value::value>, Env>::value;
	};

    /* Implementacja operacji Sum i jej przypadków szczególnych: Inc1 i Inc10. */
	template<typename ...R>
	struct AuxSum;

	template<typename Env>
	struct Eval<AuxSum<>, Env> {
		static constexpr ValueType value = 0;
	};

	template<typename Arg, typename Env, typename ...R>
	struct Eval<AuxSum<Arg, R...>, Env> {
		static constexpr ValueType value = Eval<Arg, Env>::value::value +
                                            Eval<AuxSum<R...>, Env>::value;
	};

	template<typename Env, typename ...R>
	struct Eval<Sum<R...>, Env> {
		static_assert(sizeof...(R) > 1);
		using value = Eval<AuxSum<R...>, Env>;
	};

	template <typename Arg, typename Env>
	struct Eval<Inc1<Arg>, Env> {
		using value = Eval<AuxSum<Arg, Lit<Fib<1>>>, Env>;
	};

	template <typename Arg, typename Env>
	struct Eval<Inc10<Arg>, Env> {
		using value = Eval<AuxSum<Arg, Lit<Fib<10>>>, Env>;
	};

public:
  
	/* Implementacja publicznej metody szblonowej eval dla wartości liczbowych. */
	template<typename Expr, typename X = ValueType, typename
            std::enable_if_t<std::is_integral<X>::value, int> = 0>
	static constexpr X eval() {
		return Eval<Expr, EmptyEnv>::value::value;
	}

  /* Implementacja publicznej metody szblonowej eval dla wartości innych niż liczbowe. */
	template<typename Expr, typename X = ValueType, typename
            std::enable_if_t<!std::is_integral<X>::value, int> = 0>
	
	static constexpr void eval() {
		std::cout << "Fibin doesn't support: " << typeid(ValueType).name() << std::endl;
	}
};

#endif //__FIBIN_H__