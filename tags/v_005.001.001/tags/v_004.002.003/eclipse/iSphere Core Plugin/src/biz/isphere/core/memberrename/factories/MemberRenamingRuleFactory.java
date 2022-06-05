package biz.isphere.core.memberrename.factories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import biz.isphere.core.memberrename.adapters.IMemberRenamingRuleAdapter;
import biz.isphere.core.memberrename.adapters.MemberRenamingRuleNumberAdapter;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;
import biz.isphere.core.memberrename.rules.MemberRenamingRuleNumber;

public final class MemberRenamingRuleFactory {

    /**
     * The instance of this Singleton class.
     */
    private static MemberRenamingRuleFactory instance;

    /**
     * List of backup member name rules.
     */
    private Map<String, IMemberRenamingRule> memberRenamingRules;

    /**
     * List of backup member name rule adapters.
     */
    private Map<String, IMemberRenamingRuleAdapter> memberRenamingRuleAdapters;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private MemberRenamingRuleFactory() {
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static MemberRenamingRuleFactory getInstance() {
        if (instance == null) {
            instance = new MemberRenamingRuleFactory();
            instance.initialize();
        }
        return instance;
    }

    private void initialize() {

        memberRenamingRules = new HashMap<String, IMemberRenamingRule>();
        memberRenamingRuleAdapters = new HashMap<String, IMemberRenamingRuleAdapter>();

        addAdapter(MemberRenamingRuleNumber.class, new MemberRenamingRuleNumberAdapter());
        // addAdapter(MemberRenamingRuleNumber2.class, new
        // MemberRenamingRuleNumberAdapter2());

        addRule(new MemberRenamingRuleNumber());
        // addRule(new MemberRenamingRuleNumber2());
    }

    private void addAdapter(Class<? extends IMemberRenamingRule> clazz, IMemberRenamingRuleAdapter adapter) {
        memberRenamingRuleAdapters.put(clazz.getName(), adapter);
    }

    private void addRule(IMemberRenamingRule rule) {
        memberRenamingRules.put(rule.getClass().getName(), rule);
    }

    /**
     * Returns the backup member name rule identified by a given class.
     * 
     * @param clazz - Class identifying the backup member name rule. The
     *        specified class must implement interface
     *        <code>IMemberRenamingRule</code>.
     * @return backup member name rule
     */
    public IMemberRenamingRule getMemberRenamingRule(Class<? extends IMemberRenamingRule> clazz) {
        return getMemberRenamingRule(clazz.getName());
    }

    /**
     * Returns the backup member name rule identified by a given key.
     * 
     * @param key - Key identifying the backup member name rule. The key must be
     *        a full-qualified class name of a class that implements the
     *        <code>IMemberRenamingRule</code> interface.
     * @return backup member name rule
     */
    private IMemberRenamingRule getMemberRenamingRule(String key) {
        return getMemberRenamingRulesMap().get(key);
    }

    /**
     * Returns a list of backup member name rules.
     * 
     * @return list of rules
     */
    public IMemberRenamingRule[] getRules() {

        Collection<IMemberRenamingRule> memberRenamingRules = getMemberRenamingRulesMap().values();

        return memberRenamingRules.toArray(new IMemberRenamingRule[memberRenamingRules.size()]);
    }

    /**
     * Returns the backup member name rule adapter identified by a given class.
     * 
     * @param clazz - Class identifying the backup member name rule adapter. The
     *        specified class must implement interface
     *        <code>IMemberRenamingRule</code>.
     * @return backup member name rule adapter
     */
    public IMemberRenamingRuleAdapter getMemberRenamingRuleAdapter(Class<? extends IMemberRenamingRule> clazz) {
        return getMemberRenamingRuleAdapter(clazz.getName());
    }

    /**
     * Returns the backup member name rule adapter identified by a given key.
     * 
     * @param key - Key identifying the backup member name rule adapter. The key
     *        must be a full-qualified class name of a class that implements the
     *        <code>IMemberRenamingRule</code> interface.
     * @return backup member name rule adapter
     */
    private IMemberRenamingRuleAdapter getMemberRenamingRuleAdapter(String key) {
        return getMemberRenamingRuleAdaptersMap().get(key);
    }

    /**
     * Returns a list of backup member name rule adapters.
     * 
     * @return list of adapters
     */
    public IMemberRenamingRuleAdapter[] getMemberRenamingRuleAdapters() {

        Collection<IMemberRenamingRuleAdapter> emberRenamingRuleAdapters = getMemberRenamingRuleAdaptersMap().values();

        return emberRenamingRuleAdapters.toArray(new IMemberRenamingRuleAdapter[emberRenamingRuleAdapters.size()]);
    }

    private Map<String, IMemberRenamingRule> getMemberRenamingRulesMap() {

        if (memberRenamingRules != null) {
            return memberRenamingRules;
        }

        memberRenamingRules = new HashMap<String, IMemberRenamingRule>();

        IMemberRenamingRule rule;

        rule = new MemberRenamingRuleNumber();
        memberRenamingRules.put(rule.getClass().getName(), rule);

        // rule = new MemberRenamingRuleNumber2();
        // memberRenamingRules.put(rule.getClass().getName(), rule);

        return memberRenamingRules;
    }

    private Map<String, IMemberRenamingRuleAdapter> getMemberRenamingRuleAdaptersMap() {

        if (memberRenamingRuleAdapters != null) {
            return memberRenamingRuleAdapters;
        }

        memberRenamingRuleAdapters = new HashMap<String, IMemberRenamingRuleAdapter>();

        IMemberRenamingRuleAdapter adapter;

        adapter = new MemberRenamingRuleNumberAdapter();
        memberRenamingRuleAdapters.put(MemberRenamingRuleNumber.class.getName(), adapter);

        // adapter = new MemberRenamingRuleNumberAdapter2();
        // memberRenamingRuleAdapters.put(MemberRenamingRuleNumber2.class.getName(),
        // adapter);

        return memberRenamingRuleAdapters;
    }
}