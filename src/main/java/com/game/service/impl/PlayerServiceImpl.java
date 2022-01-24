package com.game.service.impl;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.BindException;
import com.game.repository.NotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PlayerServiceImpl {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Page<Player> getAll(Specification<Player> specification, Pageable sortedByName) {
        return playerRepository.findAll(specification, sortedByName);
    }

    public List<Player> getAll(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    public Specification<Player> nameFilter(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public Specification<Player> titleFilter(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public Specification<Player> raceFilter(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    public Specification<Player> professionFilter(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    public Specification<Player> experienceFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            }
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    public Specification<Player> dateFilter(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                Date before1 = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), before1);
            }
            if (before == null) {
                Date after1 = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), after1);
            }
            Date before1 = new Date(before - 3600001);
            Date after1 = new Date(after);
            return criteriaBuilder.between(root.get("birthday"), after1, before1);
        };
    }

    public Specification<Player> usageFilter(Boolean banned) {
        return (root, query, criteriaBuilder) -> {
            if (banned == null) {
                return null;
            }
            if (banned) {
                return criteriaBuilder.isTrue(root.get("banned"));
            } else {
                return criteriaBuilder.isFalse(root.get("banned"));
            }
        };
    }

    public Specification<Player> levelFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            }
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    public Player findById(Long id) {
        if (id <= 0) {
            return playerRepository.findById(id).orElseThrow(BindException::new);
        } else {
            return playerRepository.findById(id).orElseThrow(NotFoundException::new);
        }
    }

    public Player creat(Player player) {
        try {
            if (player.getTitle().length() > 30 || player.getName().length() > 12 || player.getExperience() > 10000000) {
                throw new BindException();
            }
        } catch (NullPointerException ignored) {

        }
        if (player.getName() == null || player.getTitle() == null || player.getRace() == null
                || player.getProfession() == null || player.getBirthday() == null || player.getExperience() == null
                || player.getBanned() == null)
            throw new BindException();
        else {
            player.setLevel((int) (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
            player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
            return playerRepository.save(player);
        }
    }

    public Player update(Player player, long id) {
        Player changedPlayer = findById(id);

            if (player.getName() != null) {
                if (player.getName() == null || player.getName().isEmpty() || player.getName().length() > 12)
                    throw new BindException();
                changedPlayer.setName(player.getName());
            }
            if (player.getTitle() != null) {
                if (player.getTitle().length() > 30 || player.getTitle() == null || player.getTitle().isEmpty())
                    throw new BindException();
                changedPlayer.setTitle(player.getTitle());
            }
            if (player.getRace() != null) {
                if (player.getRace() == null)
                    throw new BindException();
                changedPlayer.setRace(player.getRace());
            }

            if (player.getProfession() != null) {
                if (player.getProfession() == null)
                    throw new BindException();
                changedPlayer.setProfession(player.getProfession());
            }

            if (player.getBirthday() != null) {
                if (player.getBirthday() == null || player.getBirthday().getTime() == 0)
                    throw new BindException();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(player.getBirthday().getTime());
                if (calendar.get(Calendar.YEAR) < 2000L || calendar.get(Calendar.YEAR) > 3000L)
                    throw new BindException();
                changedPlayer.setBirthday(player.getBirthday());
            }
            if (player.getExperience() != null) {
                if (player.getExperience() < 0 || player.getExperience() > 10000000 || player.getExperience() == null)
                    throw new BindException();
                changedPlayer.setExperience(player.getExperience());
            }
            if (player.getBanned() != null)
                changedPlayer.setBanned(player.getBanned());
        changedPlayer.setId(changedPlayer.getId());

            changedPlayer.setLevel((int) (Math.sqrt(2500 + 200 * changedPlayer.getExperience()) - 50) / 100);
            changedPlayer.setUntilNextLevel(50 * (changedPlayer.getLevel() + 1) * (changedPlayer.getLevel() + 2) - changedPlayer.getExperience());

        return playerRepository.save(changedPlayer);
    }

    public void delete(Player player) {
        playerRepository.delete(player);
    }
}
