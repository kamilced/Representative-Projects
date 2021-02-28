#include "lib_playlist.h"
#include <iostream>
#include <cassert>

int main() {
    Player player;

    // pusty plik
    try {
        auto file = player.openFile(File(""));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // plik z samymi '|'
    try {
        auto file = player.openFile(File("|||"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // plik z samym poprawnym typem 'audio'
    try {
        auto file = player.openFile(File("audio"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // plik z samym poprawnym typem 'video'
    try {
        auto file = player.openFile(File("video"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // plik z niepoprawnym typem 'type'
    try {
        auto file = player.openFile(File("type|title:unknown|artist:unknown|Content"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // plik ze "śmieciami"
    try {
        auto file = player.openFile(File("#!@^#*)#!)"));
        static_cast<void>(file);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    return 0;
}
