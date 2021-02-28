#include <iostream>
#include <cassert>
#include "lib_playlist.h"

int main() {
    Player player;
    auto song1 = player.openFile(File("audio|title:1|artist:1|Song1"));
    auto song2 = player.openFile(File("audio|title:2|artist:2|Song2"));
    auto movie3 = player.openFile(File("video|title:3|year:1990|Zbivr3"));
    auto movie4 = player.openFile(File("video|title:4|year:1990|Zbivr4"));
    auto playlist = player.createPlaylist("Playlist");
    playlist->add(song1);
    playlist->add(song2);
    playlist->add(movie3);
    playlist->add(movie4);

    // domyślne odtwarzanie sekwencyjne
    playlist->play();

    // odtwarzanie nieparzyste-parzyste
    playlist->setMode(createOddEvenMode());
    playlist->play();

    // odwarzanie losowe: ziarno 1
    playlist->setMode(createShuffleMode(1));
    playlist->play();

    // odwarzanie losowe: ziarno 2020
    playlist->setMode(createShuffleMode(2020));
    playlist->play();

    // znowu odtwarzanie sekwencyjne
    playlist->setMode(createSequenceMode());
    playlist->play();

    // usunięcie ostatniego
    playlist->remove();
    playlist->play();

    // usunięcie drugiego elementu (numeracja od 0)
    playlist->remove(1);
    playlist->play();

    // usunięcie elementu spoza zakresu
    try {
        playlist->remove(42);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    return 0;
}
