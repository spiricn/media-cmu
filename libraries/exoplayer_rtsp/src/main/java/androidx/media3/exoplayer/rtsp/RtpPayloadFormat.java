/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.media3.exoplayer.rtsp;

import static androidx.media3.common.util.Assertions.checkArgument;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.UnstableApi;
import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * Represents the payload format used in RTP.
 *
 * <p>In RTSP playback, the format information is always present in the {@link SessionDescription}
 * enclosed in the response of a DESCRIBE request. Within each track's {@link MediaDescription}, it
 * is the attributes FMTP and RTPMAP that allows us to recreate the media format.
 *
 * <p>This class wraps around the {@link Format} class, in addition to the instance fields that are
 * specific to RTP.
 */
@UnstableApi
public final class RtpPayloadFormat {

  private static final String RTP_MEDIA_AC3 = "AC3";
  private static final String RTP_MEDIA_AMR = "AMR";
  private static final String RTP_MEDIA_AMR_WB = "AMR-WB";
  private static final String RTP_MEDIA_MPEG4_GENERIC = "MPEG4-GENERIC";
  private static final String RTP_MEDIA_MPEG4_VIDEO = "MP4V-ES";
  private static final String RTP_MEDIA_H263_1998 = "H263-1998";
  private static final String RTP_MEDIA_H263_2000 = "H263-2000";
  private static final String RTP_MEDIA_H264 = "H264";
  private static final String RTP_MEDIA_H265 = "H265";
  private static final String RTP_MEDIA_OPUS = "OPUS";
  private static final String RTP_MEDIA_PCM_L8 = "L8";
  private static final String RTP_MEDIA_PCM_L16 = "L16";
  private static final String RTP_MEDIA_PCMA = "PCMA";
  private static final String RTP_MEDIA_PCMU = "PCMU";
  private static final String RTP_MEDIA_VP8 = "VP8";
  private static final String RTP_MEDIA_VP9 = "VP9";

  /** Returns whether the format of a {@link MediaDescription} is supported. */
  public static boolean isFormatSupported(MediaDescription mediaDescription) {
    switch (Ascii.toUpperCase(mediaDescription.rtpMapAttribute.mediaEncoding)) {
      case RTP_MEDIA_AC3:
      case RTP_MEDIA_AMR:
      case RTP_MEDIA_AMR_WB:
      case RTP_MEDIA_H263_1998:
      case RTP_MEDIA_H263_2000:
      case RTP_MEDIA_H264:
      case RTP_MEDIA_H265:
      case RTP_MEDIA_MPEG4_VIDEO:
      case RTP_MEDIA_MPEG4_GENERIC:
      case RTP_MEDIA_OPUS:
      case RTP_MEDIA_PCM_L8:
      case RTP_MEDIA_PCM_L16:
      case RTP_MEDIA_PCMA:
      case RTP_MEDIA_PCMU:
      case RTP_MEDIA_VP8:
      case RTP_MEDIA_VP9:
        return true;
      default:
        return false;
    }
  }

  /**
   * Gets the MIME type that is associated with the RTP media type.
   *
   * <p>For instance, RTP media type "H264" maps to {@link MimeTypes#VIDEO_H264}.
   *
   * @throws IllegalArgumentException When the media type is not supported/recognized.
   */
  public static String getMimeTypeFromRtpMediaType(String mediaType) {
    switch (Ascii.toUpperCase(mediaType)) {
      case RTP_MEDIA_AC3:
        return MimeTypes.AUDIO_AC3;
      case RTP_MEDIA_AMR:
        return MimeTypes.AUDIO_AMR_NB;
      case RTP_MEDIA_AMR_WB:
        return MimeTypes.AUDIO_AMR_WB;
      case RTP_MEDIA_MPEG4_GENERIC:
        return MimeTypes.AUDIO_AAC;
      case RTP_MEDIA_OPUS:
        return MimeTypes.AUDIO_OPUS;
      case RTP_MEDIA_PCM_L8:
      case RTP_MEDIA_PCM_L16:
        return MimeTypes.AUDIO_RAW;
      case RTP_MEDIA_PCMA:
        return MimeTypes.AUDIO_ALAW;
      case RTP_MEDIA_PCMU:
        return MimeTypes.AUDIO_MLAW;
      case RTP_MEDIA_H263_1998:
      case RTP_MEDIA_H263_2000:
        return MimeTypes.VIDEO_H263;
      case RTP_MEDIA_H264:
        return MimeTypes.VIDEO_H264;
      case RTP_MEDIA_H265:
        return MimeTypes.VIDEO_H265;
      case RTP_MEDIA_MPEG4_VIDEO:
        return MimeTypes.VIDEO_MP4V;
      case RTP_MEDIA_VP8:
        return MimeTypes.VIDEO_VP8;
      case RTP_MEDIA_VP9:
        return MimeTypes.VIDEO_VP9;
      default:
        throw new IllegalArgumentException(mediaType);
    }
  }

  /** Returns the PCM encoding type for {@code mediaEncoding}. */
  public static @C.PcmEncoding int getRawPcmEncodingType(String mediaEncoding) {
    checkArgument(
        mediaEncoding.equals(RTP_MEDIA_PCM_L8) || mediaEncoding.equals(RTP_MEDIA_PCM_L16));
    return mediaEncoding.equals(RtpPayloadFormat.RTP_MEDIA_PCM_L8)
        ? C.ENCODING_PCM_8BIT
        : C.ENCODING_PCM_16BIT_BIG_ENDIAN;
  }

  /** The payload type associated with this format. */
  public final int rtpPayloadType;
  /** The clock rate in Hertz, associated with the format. */
  public final int clockRate;
  /** The {@link Format} of this RTP payload. */
  public final Format format;
  /** The format parameters, mapped from the SDP FMTP attribute (RFC2327 Page 22). */
  public final ImmutableMap<String, String> fmtpParameters;

  /**
   * Creates a new instance.
   *
   * @param format The associated {@link Format media format}.
   * @param rtpPayloadType The assigned RTP payload type, from the RTPMAP attribute in {@link
   *     MediaDescription}.
   * @param clockRate The associated clock rate in hertz.
   * @param fmtpParameters The format parameters, from the SDP FMTP attribute (RFC2327 Page 22),
   *     empty if unset. The keys and values are specified in the RFCs for specific formats. For
   *     instance, RFC3640 Section 4.1 defines keys like profile-level-id and config.
   */
  public RtpPayloadFormat(
      Format format, int rtpPayloadType, int clockRate, Map<String, String> fmtpParameters) {
    this.rtpPayloadType = rtpPayloadType;
    this.clockRate = clockRate;
    this.format = format;
    this.fmtpParameters = ImmutableMap.copyOf(fmtpParameters);
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RtpPayloadFormat that = (RtpPayloadFormat) o;
    return rtpPayloadType == that.rtpPayloadType
        && clockRate == that.clockRate
        && format.equals(that.format)
        && fmtpParameters.equals(that.fmtpParameters);
  }

  @Override
  public int hashCode() {
    int result = 7;
    result = 31 * result + rtpPayloadType;
    result = 31 * result + clockRate;
    result = 31 * result + format.hashCode();
    result = 31 * result + fmtpParameters.hashCode();
    return result;
  }
}
