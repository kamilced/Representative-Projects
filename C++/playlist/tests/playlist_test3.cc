#include <iostream>
#include <cassert>
#include "lib_playlist.h"

int main() {
    Player player;

    try {
        auto file = player.openFile(File("audio|artist:unknown|Song"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    try {
        auto file = player.openFile(File("audio|title:unknown|Song"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    try {
        auto file = player.openFile(File("audio|title:unknown|artist:unknown|+"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // poprawna piosenka
    auto song1 = player.openFile(File("audio|title:unknown|artist:unknown|Song"));
    song1->play();

    // poprawna piosenka - nieważna kolejność metadanych
    auto song2 = player.openFile(File("audio|artist:unknown|title:unknown|Song"));
    song2->play();

    // poprawna piosenka - dodatkowe metadane
    auto song3 = player.openFile(File("audio|artist:unknown|title:unknown|extra:unknown|meta:unknown|Song"));
    song3->play();

    // poprawna piosenka - wartość metadanej z ':'
    auto song4 = player.openFile(File("audio|artist:unknown:unknown|title:unknown|extra:unknown|meta:unknown|Song"));
    song4->play();

    // poprawna piosenka - puste metadane
    auto song5 = player.openFile(File("audio|artist:|title:|extra:|meta:|Song"));
    song5->play();

    // poprawna piosenka - wartość metadanej z wielokrotnym ':'
    auto song6 = player.openFile(File("audio|artist:::::|title:|extra:|meta:|Song"));
    song6->play();

    return 0;
}
