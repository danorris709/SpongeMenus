package me.dnorris.sponge.api.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class UtilConfig {

	public Optional<Boolean> getBoolean(CommentedConfigurationNode node) {
		return Optional.ofNullable(node.getBoolean());
	}

	public Optional<Double> getDouble(CommentedConfigurationNode node) {
		return Optional.ofNullable(node.getDouble());
	}

	public Optional<Float> getFloat(CommentedConfigurationNode node) {
		return Optional.ofNullable(node.getFloat());
	}

	public Optional<Integer> getInt(CommentedConfigurationNode node) {
		return Optional.ofNullable(node.getInt());
	}

	public Optional<Long> getLong(CommentedConfigurationNode node) {
		return Optional.ofNullable(node.getLong());
	}

	public Optional<String> getString(CommentedConfigurationNode node) {
		return Optional.ofNullable(node.getString());
	}

	public Optional<Object> getValue(CommentedConfigurationNode node) {
		return Optional.ofNullable(node.getValue());
	}
}
