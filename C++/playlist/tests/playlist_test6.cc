#include <iostream>
#include <cassert>
#include "lib_playlist.h"

int main() {
    Player player;
    auto song1 = player.openFile(File("audio|title:1|artist:1|Song1"));
    auto song2 = player.openFile(File("audio|title:2|artist:2|Song2"));
    auto movie3 = player.openFile(File("video|title:3|year:1990|Zbivr3"));
    auto movie4 = player.openFile(File("video|title:4|year:1990|Zbivr4"));
    auto song5 = player.openFile(File("audio|title:5|artist:5|Song5"));
    auto song6 = player.openFile(File("audio|title:6|artist:6|Song6"));
    auto playlist1 = player.createPlaylist("Playlist1");
    playlist1->add(song1);
    playlist1->add(song2);
    playlist1->add(movie3);
    playlist1->add(movie4);

    auto playlist2 = player.createPlaylist("Playlist2");
    playlist2->add(playlist1);
    playlist2->add(song5);
    playlist2->add(song6);

    // domyślne sposoby odtwarzanie sekwencyjne
    playlist2->play();

    playlist1->setMode(createOddEvenMode());
    playlist2->play();

    playlist1->setMode(createShuffleMode(1));
    playlist2->play();

    playlist1->setMode(createShuffleMode(2020));
    playlist2->play();

    playlist2->setMode(createOddEvenMode());
    playlist1->setMode(createSequenceMode());
    playlist2->play();

    playlist2->setMode(createShuffleMode(2));
    playlist1->setMode(createSequenceMode());
    playlist2->play();

    playlist2->setMode(createOddEvenMode());
    playlist2->add(playlist1);
    playlist1->setMode(createOddEvenMode());
    playlist2->play();

    // bezpośrednie zapętlenie
    try {
        playlist1->add(playlist2);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // dodanie listy do siebie
    try {
        playlist1->add(playlist1);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    // cykl 'dziecko wśród rodziców'
    auto playlist3 = player.createPlaylist("Playlist3");
    auto playlist4 = player.createPlaylist("Playlist4");
    auto playlist5 = player.createPlaylist("Playlist5");
    auto playlist6 = player.createPlaylist("Playlist6");
    auto playlist7 = player.createPlaylist("Playlist7");
    playlist3->add(playlist6);
    playlist3->add(playlist7);
    playlist4->add(playlist6);
    playlist5->add(playlist2);
    playlist6->add(playlist2);
    playlist6->add(playlist1);
    playlist7->add(playlist4);
    playlist3->play();
    try {
        playlist6->add(playlist7);
        assert(false);
    } catch (PlayerException const& e) {
        // oczekiwany wyjątek
    }

    return 0;
}
