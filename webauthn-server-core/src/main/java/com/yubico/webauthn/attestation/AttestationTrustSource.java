// Copyright (c) 2018, Yubico AB
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.yubico.webauthn.attestation;

import com.yubico.webauthn.data.ByteArray;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Abstraction of a repository which can look up trust roots for authenticator attestation. */
public interface AttestationTrustSource {

  /**
   * Attempt to look up attestation trust roots for an authenticator AAGUID.
   *
   * @param aaguid the AAGUID of an authenticator to be assessed for trustworthiness
   * @return A set of attestation root certificates trusted to attest for this AAGUID, if any are
   *     available. If no trust roots for this AAGUID are found, or if authenticators with this
   *     AAGUID are not trusted, return an empty set. Implementations MAY reuse the same result set
   *     for multiple calls of this method, even with different AAGUID arguments, but MUST return an
   *     empty set for AAGUIDs that should not be trusted.
   */
  Set<X509Certificate> findTrustRoots(ByteArray aaguid);

  /**
   * Attempt to look up attestation trust roots for an attestation certificate chain.
   *
   * <p>Note that it is possible for the same trust root to be used for different certificate
   * chains. For example, an authenticator vendor may make two different authenticator models, each
   * with its own attestation leaf certificate but both signed by the same attestation root
   * certificate. If a Relying Party trusts one of those authenticators models but not the other,
   * then its implementation of this method MUST return an empty set for the untrusted certificate
   * chain.
   *
   * @param attestationCertificateChain a certificate chain from an authenticator to be assessed for
   *     trustworthiness. The trust anchor is typically not included in this certificate chain.
   * @return A set of attestation root certificates trusted to attest for this attestation
   *     certificate chain, if any are available. If the certificate chain is empty, or if no trust
   *     roots for this certificate chain are found, or if authenticators with this certificate
   *     chain are not trusted, return an empty set. Implementations MAY reuse the same result set
   *     for multiple calls of this method, even with different arguments, but MUST return an empty
   *     set for certificate chains that should not be trusted.
   */
  Set<X509Certificate> findTrustRoots(List<X509Certificate> attestationCertificateChain);

  /**
   * Retrieve a {@link CertStore} containing additional CRLs and/or intermediates certificates
   * required for validating the given certificate chain.
   *
   * <p>Any certificates included in this {@link CertStore} are NOT considered trusted. For adding
   * trusted attestation roots, see {@link #findTrustRoots(List)} and {@link
   * #findTrustRoots(ByteArray)}.
   *
   * <p>The default implementation always returns {@link Optional#empty()}. This method is most
   * likely useful for tests, since most real-world certificates will likely include the X.509 CRL
   * distribution points extension, in which case an additional {@link CertStore} is not necessary.
   *
   * @param attestationCertificateChain a certificate chain, where each certificate in the list
   *     should be signed by the subsequent certificate. The trust anchor is typically not included
   *     in this certificate chain.
   * @return a {@link CertStore} containing any additional CRLs and/or intermediate certificates
   *     required for validating the certificate chain, if applicable. Implementations MAY reuse the
   *     same {@link CertStore} instance for multiple calls of this method, even with different
   *     arguments.
   */
  default Optional<CertStore> getCertStore(List<X509Certificate> attestationCertificateChain) {
    return Optional.empty();
  }
}
