package se.michaelthelin.spotify.requests.data.follow;

import org.apache.hc.core5.http.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.michaelthelin.spotify.TestUtil;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.data.AbstractDataTest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static se.michaelthelin.spotify.Assertions.assertHasBodyParameter;
import static se.michaelthelin.spotify.Assertions.assertHasHeader;

@RunWith(MockitoJUnitRunner.class)
public class UnfollowArtistsOrUsersRequestTest extends AbstractDataTest<String> {
  private final UnfollowArtistsOrUsersRequest defaultRequest = SPOTIFY_API
    .unfollowArtistsOrUsers(ModelObjectType.ARTIST, new String[]{ID_ARTIST, ID_ARTIST})
    .setHttpManager(
      TestUtil.MockedHttpManager.returningJson(null))
    .build();
  private final UnfollowArtistsOrUsersRequest bodyRequest = SPOTIFY_API
    .unfollowArtistsOrUsers(ModelObjectType.ARTIST, ARTISTS)
    .setHttpManager(
      TestUtil.MockedHttpManager.returningJson(null))
    .build();

  public UnfollowArtistsOrUsersRequestTest() throws Exception {
  }

  @Test
  public void shouldComplyWithReference() {
    assertHasAuthorizationHeader(defaultRequest);
    assertEquals(
      "https://api.spotify.com:443/v1/me/following?type=ARTIST&ids=0LcJLqbBmaGUft1e9Mm8HV%2C0LcJLqbBmaGUft1e9Mm8HV",
      defaultRequest.getUri().toString());

    assertHasAuthorizationHeader(bodyRequest);
    assertHasHeader(defaultRequest, "Content-Type", "application/json");
    assertHasBodyParameter(bodyRequest,
      "ids",
      ARTISTS);
    assertEquals("https://api.spotify.com:443/v1/me/following?type=ARTIST",
      bodyRequest.getUri().toString());
  }

  @Test
  public void shouldReturnDefault_sync() throws IOException, SpotifyWebApiException, ParseException {
    shouldReturnDefault(defaultRequest.execute());
  }

  @Test
  public void shouldReturnDefault_async() throws ExecutionException, InterruptedException {
    shouldReturnDefault(defaultRequest.executeAsync().get());
  }

  public void shouldReturnDefault(final String string) {
    assertNull(
      string);
  }
}