package com.wrapper.spotify.requests;

import com.wrapper.spotify.IHttpManager;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyApiThreading;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;

public abstract class AbstractRequest implements IRequest {

  private IHttpManager httpManager;
  private URI uri;
  private List<Header> headers;
  private List<NameValuePair> formParameters;
  private List<NameValuePair> bodyParameters;
  private String body;

  protected AbstractRequest(Builder<?> builder) {
    assert (builder != null);
    assert (builder.path != null);
    assert (!builder.path.equals(""));

    this.httpManager = builder.httpManager;

    URIBuilder uriBuilder = new URIBuilder();
    uriBuilder
            .setScheme(builder.scheme)
            .setHost(builder.host)
            .setPort(builder.port)
            .setPath(builder.path);
    if (builder.queryParameters.size() > 0) {
      uriBuilder
              .setParameters(builder.queryParameters);
    }

    try {
      this.uri = uriBuilder.build();
    } catch (URISyntaxException e) {
      SpotifyApi.LOGGER.log(Level.SEVERE, e.getMessage());
    }

    this.headers = builder.headers;
    this.formParameters = builder.formParameters;
    this.bodyParameters = builder.bodyParameters;
    this.body = builder.body;
  }

  /**
   * Get something asynchronously.
   *
   * @return A {@link Future} for a generic.
   */
  public <T> Future<T> executeAsync() {
    return SpotifyApiThreading.executeAsync(
            new Callable<T>() {
              public T call() throws IOException, SpotifyWebApiException {
                return execute();
              }
            });
  }

  public String getJson() throws
          IOException,
          SpotifyWebApiException {
    return httpManager.get(uri, headers.toArray(new Header[headers.size()]));
  }

  public String postJson() throws
          IOException,
          SpotifyWebApiException {
    return httpManager.post(uri, headers.toArray(new Header[headers.size()]), formParameters);
  }

  public String putJson() throws
          IOException,
          SpotifyWebApiException {
    return httpManager.put(uri, headers.toArray(new Header[headers.size()]), formParameters);
  }

  public String deleteJson() throws
          IOException,
          SpotifyWebApiException {
    return httpManager.delete(uri, headers.toArray(new Header[headers.size()]));
  }

  public IHttpManager getHttpManager() {
    return httpManager;
  }

  public URI getUri() {
    return uri;
  }

  public List<Header> getHeaders() {
    return headers;
  }

  public List<NameValuePair> getFormParameters() {
    return formParameters;
  }

  public List<NameValuePair> getBodyParameters() {
    return bodyParameters;
  }

  public String getBody() {
    return body;
  }

  public static abstract class Builder<T extends Builder<?>> implements IRequest.Builder {

    private final List<NameValuePair> pathParameters = new ArrayList<>();
    private final List<NameValuePair> queryParameters = new ArrayList<>();
    private final List<Header> headers = new ArrayList<>();
    private final List<NameValuePair> formParameters = new ArrayList<>();
    private final List<NameValuePair> bodyParameters = new ArrayList<>();
    private IHttpManager httpManager = SpotifyApi.DEFAULT_HTTP_MANAGER;
    private String scheme = SpotifyApi.DEFAULT_SCHEME;
    private String host = SpotifyApi.DEFAULT_HOST;
    private Integer port = SpotifyApi.DEFAULT_PORT;
    private String path = null;
    private String body = null;

    protected Builder() {
    }

    @SuppressWarnings("unchecked")
    public T setHttpManager(final IHttpManager httpManager) {
      assert (httpManager != null);
      this.httpManager = httpManager;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setScheme(final String scheme) {
      assert (scheme != null);
      assert (!scheme.equals(""));
      this.scheme = scheme;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setHost(final String host) {
      assert (host != null);
      assert (!scheme.equals(""));
      this.host = host;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setPort(final Integer port) {
      assert (port != null);
      assert (port >= 0);
      this.port = port;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setPath(final String path) {
      assert (path != null);
      assert (!path.equals(""));

      String builtPath = path;

      for (NameValuePair nameValuePair : pathParameters) {
        builtPath = builtPath.replaceAll("\\{" + nameValuePair.getName() + "}", nameValuePair.getValue());
      }

      this.path = builtPath;
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setPathParameter(final String name, final String value) {
      assert (name != null && value != null);
      assert (!name.equals("") && !value.equals(""));

      String encodedValue = null;

      try {
        encodedValue = URLEncoder.encode(value, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        SpotifyApi.LOGGER.log(Level.SEVERE, e.getMessage());
      }

      this.pathParameters.add(new BasicNameValuePair(name, encodedValue));
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setDefaults(final IHttpManager httpManager,
                         final String scheme,
                         final String host,
                         final Integer port) {
      setHttpManager(httpManager);
      setScheme(scheme);
      setHost(host);
      setPort(port);

      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <X> T setQueryParameter(final String name, final X value) {
      assert (name != null);
      assert (!name.equals(""));
      assert (value != null);
      this.queryParameters.add(new BasicNameValuePair(name, String.valueOf(value)));
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <X> T setHeader(final String name, final X value) {
      assert (name != null);
      assert (!name.equals(""));
      assert (value != null);
      this.headers.add(new BasicHeader(name, String.valueOf(value)));
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <X> T setFormParameter(final String name, final X value) {
      assert (name != null);
      assert (!name.equals(""));
      assert (value != null);
      this.formParameters.add(new BasicNameValuePair(name, String.valueOf(value)));
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <X> T setBodyParameter(final String name, final X value) {
      assert (name != null);
      assert (!name.equals(""));
      assert (value != null);
      this.bodyParameters.add(new BasicNameValuePair(name, String.valueOf(value)));
      return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setBody(final String value) {
      this.body = value;
      return (T) this;
    }
  }
}
