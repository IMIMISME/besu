/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.ethereum.api.jsonrpc.internal.results.tracing;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import org.hyperledger.besu.ethereum.api.jsonrpc.internal.processor.TransactionTrace;
import org.hyperledger.besu.ethereum.api.jsonrpc.internal.results.Quantity;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.Gas;
import org.hyperledger.besu.ethereum.core.Transaction;
import org.hyperledger.besu.ethereum.core.Wei;
import org.hyperledger.besu.ethereum.debug.TraceFrame;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;

@JsonInclude(NON_NULL)
public class Action {

  private final String callType;
  private final String from;
  private final String gas;
  private final String input;
  private final String to;
  private final String init;
  private final String value;
  private final String address;
  private final String balance;
  private final String refundAddress;

  private Action(
      final String callType,
      final String from,
      final String gas,
      final String input,
      final String to,
      final String init,
      final String value,
      final String address,
      final String balance,
      final String refundAddress) {
    this.callType = callType;
    this.from = from;
    this.gas = gas;
    this.input = input;
    this.to = to;
    this.init = init;
    this.value = value;
    this.address = address;
    this.balance = balance;
    this.refundAddress = refundAddress;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder createCallAction(
      final Transaction transaction,
      final String lastContractAddress,
      final Address contractCallAddress,
      final TraceFrame traceFrame,
      final Gas gasRemaining) {
    return builder()
        .from(lastContractAddress)
        .to(contractCallAddress.toString())
        .input(dumpMemory(traceFrame.getMemory()))
        .gas(gasRemaining.toHexString())
        .callType("call")
        .value(Quantity.create(transaction.getValue()));
  }

  public static Builder createSelfDestructAction(
      final String lastContractAddress, final Address contractCallAddress, final Wei balance) {
    return builder()
        .address(lastContractAddress)
        .refundAddress(contractCallAddress.toString())
        .balance(balance.toShortHexString());
  }

  private static String dumpMemory(final Optional<Bytes32[]> memory) {
    return memory
        .map(
            element ->
                "0x"
                    .concat(
                        Arrays.stream(element)
                            .map(Bytes::toUnprefixedHexString)
                            .collect(Collectors.joining())))
        .orElse("");
  }

  public String getCallType() {
    return callType;
  }

  public String getFrom() {
    return from;
  }

  public String getGas() {
    return gas;
  }

  public String getInput() {
    return input;
  }

  public String getTo() {
    return to;
  }

  public String getValue() {
    return value;
  }

  public String getInit() {
    return init;
  }

  public String getAddress() {
    return address;
  }

  public String getBalance() {
    return balance;
  }

  public String getRefundAddress() {
    return refundAddress;
  }

  public static final class Builder {
    private String callType;
    private String from;
    private String gas;
    private String input;
    private String to;
    private String init;
    private String value;
    private String address;
    private String balance;
    private String refundAddress;

    private Builder() {}

    public static Builder of(final Action action) {
      final Builder builder = new Builder();
      builder.callType = action.callType;
      builder.from = action.from;
      builder.gas = action.gas;
      builder.input = action.input;
      builder.to = action.to;
      builder.init = action.init;
      builder.value = action.value;
      builder.address = action.address;
      builder.refundAddress = action.refundAddress;
      builder.balance = action.balance;
      return builder;
    }

    public static Builder from(final TransactionTrace trace) {
      return new Builder()
          .from(trace.getTransaction().getSender().toHexString())
          .gas(trace.getTraceFrames().get(0).getGasRemaining().toHexString())
          .value(Quantity.create(trace.getTransaction().getValue()));
    }

    public Builder callType(final String callType) {
      this.callType = callType;
      return this;
    }

    public Builder from(final String from) {
      this.from = from;
      return this;
    }

    public Builder gas(final String gas) {
      this.gas = gas;
      return this;
    }

    public Builder input(final String input) {
      this.input = input;
      return this;
    }

    public Builder to(final String to) {
      this.to = to;
      return this;
    }

    public Builder init(final String init) {
      this.init = init;
      return this;
    }

    public Builder value(final String value) {
      this.value = value;
      return this;
    }

    public Builder address(final String address) {
      this.address = address;
      return this;
    }

    public Builder balance(final String balance) {
      this.balance = balance;
      return this;
    }

    public Builder refundAddress(final String refundAddress) {
      this.refundAddress = refundAddress;
      return this;
    }

    public String getGas() {
      return gas;
    }

    public Action build() {
      return new Action(
          callType, from, gas, input, to, init, value, address, balance, refundAddress);
    }
  }
}
