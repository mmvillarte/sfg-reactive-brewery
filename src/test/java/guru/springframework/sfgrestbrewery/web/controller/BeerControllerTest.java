package guru.springframework.sfgrestbrewery.web.controller;

import guru.springframework.sfgrestbrewery.bootstrap.BeerLoader;
import guru.springframework.sfgrestbrewery.services.BeerService;
import guru.springframework.sfgrestbrewery.web.model.BeerDto;
import guru.springframework.sfgrestbrewery.web.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    BeerService beerService;

    BeerDto defaultBeerDto;

    BeerPagedList defaulBeerPagedList;

    @BeforeEach
    void setUp() {
        defaultBeerDto = BeerDto.builder()
                .beerName("Test beer")
                .beerStyle("PALE_ALE")
                .upc(BeerLoader.BEER_1_UPC)
                .build();

        defaulBeerPagedList = new BeerPagedList(List.of(defaultBeerDto));
    }

    @Test
    void getBeerById() {
        Integer beerId = 1;
        given(beerService.getById(any(), any())).willReturn(Mono.just(defaultBeerDto));

        webTestClient.get()
                .uri("/api/v1/beer/" + beerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(beerDto -> beerDto.getBeerName(), equalTo(defaultBeerDto.getBeerName()));
    }

    @Test
    void getBeerByUpc() {
        given(beerService.getByUpc(any())).willReturn(Mono.just(defaultBeerDto));

        webTestClient.get()
                .uri("/api/v1/beerUpc/" + BeerLoader.BEER_1_UPC)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(beerDto -> beerDto.getBeerName(), equalTo(defaultBeerDto.getBeerName()));
    }

    @Test
    void listBeers() {
        given(beerService.listBeers(any(), any(), any(), any())).willReturn(defaulBeerPagedList);

        webTestClient.get()
                .uri("/api/v1/beer/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerPagedList.class)
                .value(beerPagedList -> beerPagedList.get().findFirst().get().getBeerName(), equalTo(defaultBeerDto.getBeerName()));
    }
}