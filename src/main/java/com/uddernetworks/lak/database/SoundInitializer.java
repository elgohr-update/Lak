package com.uddernetworks.lak.database;

import com.uddernetworks.lak.keys.DefaultKey;
import com.uddernetworks.lak.keys.KeyEnum;
import com.uddernetworks.lak.sounds.DefaultSoundVariant;
import com.uddernetworks.lak.sounds.FileSound;
import com.uddernetworks.lak.sounds.SoundManager;
import com.uddernetworks.lak.sounds.SoundVariant;
import com.uddernetworks.lak.sounds.modulation.SoundModulation;
import com.uddernetworks.lak.sounds.modulation.SoundModulationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.uddernetworks.lak.Utility.getUUIDFromBytes;

/**
 * Initializes the sound data for the database. This is not done in {@link SoundRepository} as to prevent cyclical
 * dependencies with {@link SoundManager}, and to keep that class specifically for accessing and updating tables.
 */
@Component
public class SoundInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoundInitializer.class);

    private final JdbcTemplate jdbc;
    private final SoundManager soundManager;
    private final SoundModulationFactory soundModulationFactory;

    public SoundInitializer(JdbcTemplate jdbc,
                            @Qualifier("variableSoundManager") SoundManager soundManager,
                            @Qualifier("standardSoundModulationFactory") SoundModulationFactory soundModulationFactory) {
        this.jdbc = jdbc;
        this.soundManager = soundManager;
        this.soundModulationFactory = soundModulationFactory;
    }

    @PostConstruct
    public void init() {
        LOGGER.debug("Initializing sounds");

        soundManager.setSounds(jdbc.query("SELECT * FROM `sounds`;", (rs, index) ->
                new FileSound(getUUIDFromBytes(rs.getBytes("sound_id")), URI.create(rs.getString("path")))));

        soundManager.setVariants(jdbc.query("SELECT * FROM `sound_variants`;", (rs, index) ->
                new DefaultSoundVariant(getUUIDFromBytes(rs.getBytes("variant_id")),
                        soundManager.getSound(getUUIDFromBytes(rs.getBytes("sound_id"))).orElse(null),
                        rs.getString("description"),
                        new Color(Integer.parseInt(rs.getString("color"), 16)))));

        // It is not just added one by one, as adding individually may cause issues down the line depending on the
        // implementation of SoundVariant's handling of adding individual modulators.
        jdbc.query("SELECT * FROM `modulators`;", (rs, index) -> {
            var variantOptional = soundManager.getVariant(getUUIDFromBytes(rs.getBytes("variant_id")));
            if (variantOptional.isPresent()) {
                return soundModulationFactory.deserialize(variantOptional.get(), rs.getBytes("value"));
            }
            return Optional.<SoundModulation>empty();
        }).stream()
                .flatMap(Optional::stream)
                .collect(Collectors.groupingBy(SoundModulation::getSoundVariant))
                .forEach(SoundVariant::setModulators);
    }
}
