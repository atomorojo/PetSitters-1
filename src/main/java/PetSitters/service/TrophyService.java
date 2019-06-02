package PetSitters.service;

import PetSitters.domain.Animal;
import PetSitters.entity.Message;
import PetSitters.entity.UserPetSitters;
import PetSitters.entity.Valuation;
import PetSitters.repository.MessageRepository;
import PetSitters.repository.UserRepository;
import PetSitters.repository.ValuationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class TrophyService {
    @Autowired
    UserRepository UserRep;
    @Autowired
    MessageRepository MessageRep;
    @Autowired
    ValuationRepository ValuationRep;

    public void trophy01(UserPetSitters user) {
        if (user.getImage() != null && user.getDescription() != null && user.getAvailability() != null && user.getExpert() != null) {
            Boolean[] troph=user.getTrophy();
            troph[0]=true;
            user.setTrophy(troph);
            UserRep.save(user);
            trophy05(user);
        }
    }

    public void trophy02(UserPetSitters user) {
        if (user.getFavorites().size() > 0) {
            Boolean[] troph=user.getTrophy();
            troph[1]=true;
            user.setTrophy(troph);
            UserRep.save(user);
            trophy05(user);
        }
    }
    public void trophy03(UserPetSitters user) {
        Boolean[] troph=user.getTrophy();
        troph[2]=true;
        user.setTrophy(troph);
        UserRep.save(user);
        trophy05(user);
    }

    public void trophy04(UserPetSitters user) {
        Boolean[] troph=user.getTrophy();
        if (troph[15]&&troph[18]&&troph[22]&&troph[24]&&troph[27]&&troph[30]&&troph[33]&&troph[36]&&troph[39]&&troph[42]) {
            troph[3]=true;
            user.setTrophy(troph);
            UserRep.save(user);
            trophy05(user);
        }
    }

    public void trophy05(UserPetSitters user) {
        Boolean[] troph=user.getTrophy();
        if (troph[0]&&troph[1]&&troph[2]&&troph[3]&&troph[5]&&troph[6]&&troph[7]&&
                troph[8]&&troph[9]&&troph[10]&&troph[11]&&troph[12]&&troph[13]&&troph[14]&&troph[15]&&
                troph[16]&&troph[17]&&troph[18]&&troph[19]&&troph[20]&&troph[21]&&troph[22]&&troph[23]&&
                troph[24]&&troph[25]&&troph[26]&&troph[27]&&troph[28]&&troph[29]&&troph[30]&&troph[31]&&
                troph[32]&&troph[33]&&troph[34]&&troph[35]&&troph[36]&&troph[37]&&troph[38]&&troph[39]&&
                troph[40]&&troph[41]&&troph[42]&&troph[43]&&troph[44]&&troph[45]) {
            troph[4]=true;
            user.setTrophy(troph);
            UserRep.save(user);
        }
    }

    public void trophy06(UserPetSitters user) {
        if (Math.round(user.getStars())>=5) {
            Boolean[] troph=user.getTrophy();
            troph[5]=true;
            user.setTrophy(troph);
            UserRep.save(user);
            trophy05(user);
        }
    }

    public void trophy07(UserPetSitters user) {
        if (Math.round(user.getStars())>=4) {
            Boolean[] troph=user.getTrophy();
            troph[6]=true;
            user.setTrophy(troph);
            UserRep.save(user);
        }
    }

    public void trophy08(UserPetSitters user) {
        if (Math.round(user.getStars())>=3) {
            Boolean[] troph=user.getTrophy();
            troph[7]=true;
            user.setTrophy(troph);
            UserRep.save(user);
        }
    }

    public void trophy09(UserPetSitters user) {
        if (ValuationRep.findByUserWhoValues(user.getUsername()).size()>=1) {
            Boolean[] troph=user.getTrophy();
            troph[8]=true;
            user.setTrophy(troph);
            UserRep.save(user);
        }
    }
    public void trophy10(UserPetSitters user) {
        if (ValuationRep.findByUserWhoValues(user.getUsername()).size()>=5) {
            Boolean[] troph=user.getTrophy();
            troph[9]=true;
            user.setTrophy(troph);
            UserRep.save(user);
        }
    }
    public void trophy11(UserPetSitters user) {
        if (ValuationRep.findByUserWhoValues(user.getUsername()).size()>=10) {
            Boolean[] troph=user.getTrophy();
            troph[10]=true;
            user.setTrophy(troph);
            UserRep.save(user);
        }
    }

    public void trophy12_14(UserPetSitters user) {
        List<Message> why=MessageRep.findByUserWhoSendsAndIsVisible(user.getUsername(),true);
        Boolean[] troph=user.getTrophy();
        if (why.size()>=100) {
            troph[11]=true;
            if (why.size()>=500) {
                troph[12]=true;
                if (why.size()>=1000) {
                    troph[13]=true;
                }
            }
            user.setTrophy(troph);
            UserRep.save(user);
        }
    }

    public void trophy15(UserPetSitters user) {
        Boolean[] troph=user.getTrophy();
        troph[14]=true;
        user.setTrophy(troph);
        UserRep.save(user);
        trophy05(user);
    }

    public void trophy16_45(UserPetSitters user) {
        int other=0;
        int dog=0;
        int ferret=0;
        int cat=0;
        int bird=0;
        int reptile=0;
        int rodent=0;
        int fish=0;
        int amphibian=0;
        int arthoprod=0;
        List<String> animalsCaredFor=getAnimalsCaredFor(user);
        Boolean[] troph=user.getTrophy();
        for (String s:animalsCaredFor) {
            if (s.equals("Others")) {
                other++;
            }
            else if (s.equals("Dogs")) {
                dog++;
            }
            else if (s.equals("Ferrets")) {
                ferret++;
            }
            else if (s.equals("Cats")) {
                cat++;
            }
            else if (s.equals("Birds")) {
                bird++;
            }
            else if (s.equals("Reptiles")) {
                reptile++;
            }
            else if (s.equals("Rodents")) {
                rodent++;
            }
            else if (s.equals("Fishes")) {
                fish++;
            }
            else if (s.equals("Amphibians")) {
                amphibian++;
            }
            else if (s.equals("Arthoprods")) {
                arthoprod++;
            }
        }
        if (other > 0) {
            troph[15] = true;
            trophy04(user);
            if (other > 4) {
                troph[16] = true;
                if (other > 9) {
                    troph[17] = true;
                }
            }
        }
        if (dog > 0) {
            troph[18] = true;
            trophy04(user);
            if (dog > 4) {
                troph[19] = true;
                if (dog > 9) {
                    troph[20] = true;
                }
            }
        }
        if (cat > 0) {
            troph[21] = true;
            trophy04(user);
            if (cat > 4) {
                troph[22] = true;
                if (cat > 9) {
                    troph[23] = true;
                }
            }
        }
        if (ferret > 0) {
            troph[24] = true;
            trophy04(user);
            if (ferret > 4) {
                troph[25] = true;
                if (ferret > 9) {
                    troph[26] = true;
                }
            }
        }
        if (reptile > 0) {
            troph[27] = true;
            trophy04(user);
            if (reptile > 4) {
                troph[28] = true;
                if (reptile > 9) {
                    troph[29] = true;
                }
            }
        }
        if (bird > 0) {
            troph[30] = true;
            trophy04(user);
            if (bird > 4) {
                troph[31] = true;
                if (bird > 9) {
                    troph[32] = true;
                }
            }
        }

        if (rodent > 0) {
            troph[33] = true;
            trophy04(user);
            if (rodent > 4) {
                troph[34] = true;
                if (rodent > 9) {
                    troph[35] = true;
                }
            }
        }
        if (fish > 0) {
            troph[36] = true;
            trophy04(user);
            if (fish > 4) {
                troph[37] = true;
                if (fish > 9) {
                    troph[38] = true;
                }
            }
        }
        if (amphibian > 0) {
            troph[39] = true;
            trophy04(user);
            if (amphibian > 4) {
                troph[40] = true;
                if (amphibian > 9) {
                    troph[41] = true;
                }
            }
        }
        if (arthoprod > 0) {
            troph[42] = true;
            trophy04(user);
            if (arthoprod > 4) {
                troph[43] = true;
                if (arthoprod > 9) {
                    troph[44] = true;
                }
            }
        }
        user.setTrophy(troph);
        UserRep.save(user);
        trophy05(user);
    }

    private List<String> getAnimalsCaredFor(UserPetSitters user) {
        List<Animal> strings=new ArrayList<Animal>();
        List<Valuation> vals=ValuationRep.findByValuedUser(user.getUsername());
        for (Valuation val:vals) {
            strings.addAll(val.getAnimals());
        }
        List<String> ret=new ArrayList<String>();
        for (Animal a:strings) {
            ret.add(a.getTipus());
        }
        return ret;
    }

}
