#include <iostream>
#include <cassert>
#include "lib_playlist.h"

int main() {
    Player player;

    // brak title
    try {
        auto file = player.openFile(File("video|year:2020|Zbivr"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // brak year
    try {
        auto file = player.openFile(File("video|title:unknown|Zbivr"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // niepoprawna treść
    try {
        auto file = player.openFile(File("video|title:unknown|year:2020|+"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // niepoprawny year
    try {
        auto file = player.openFile(File("video|title:unknown|year:abc|Zbivr"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // poprawny plik video
    auto movie1 = player.openFile(File("video|title:unknown|year:2020|Zbivr"));
    movie1->play();

    // poprawny plik video - nieważna kolejność metadanych
    auto movie2 = player.openFile(File("video|year:2020|title:unknown|Zbivr"));
    movie2->play();

    // poprawny plik video - dodatkowe metadane
    auto movie3 = player.openFile(File("video|year:2020|title:unknown|extra:unknown|meta:unknown|Zbivr"));
    movie3->play();

    // poprawny plik video - wartość metadanej title z ':'
    auto movie4 = player.openFile(File("video|title:unknown:unknown|year:2020|extra:unknown|meta:unknown|Zbivr"));
    movie4->play();

    // poprawny plik video - puste metadane
    auto movie5 = player.openFile(File("video|title:|year:2020|extra:|meta:|Zbivr"));
    movie5->play();

    // poprawny plik video - wiele ':'
    auto movie6 = player.openFile(File("video|title:::::|year:2020|extra:|meta:|Zbivr"));
    movie6->play();

    // poprawny plik video - wszystkie znaki dopuszczalne znaki alfabetu oraz specjalne w treści
    auto movie7 = player.openFile(File("video|title:unknown|year:2020|qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ,.!?':;-"));
    movie7->play();

    return 0;
}
