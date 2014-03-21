/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package me.distributedaccounts.mgmt.service.event;
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class AccountData extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AccountData\",\"namespace\":\"me.distributedaccounts.mgmt.service.event\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"balance\",\"type\":\"float\"},{\"name\":\"active\",\"type\":\"boolean\"},{\"name\":\"description\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence id;
  @Deprecated public float balance;
  @Deprecated public boolean active;
  @Deprecated public java.lang.CharSequence description;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use {@link \#newBuilder()}. 
   */
  public AccountData() {}

  /**
   * All-args constructor.
   */
  public AccountData(java.lang.CharSequence id, java.lang.Float balance, java.lang.Boolean active, java.lang.CharSequence description) {
    this.id = id;
    this.balance = balance;
    this.active = active;
    this.description = description;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return balance;
    case 2: return active;
    case 3: return description;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.CharSequence)value$; break;
    case 1: balance = (java.lang.Float)value$; break;
    case 2: active = (java.lang.Boolean)value$; break;
    case 3: description = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   */
  public java.lang.CharSequence getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.CharSequence value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'balance' field.
   */
  public java.lang.Float getBalance() {
    return balance;
  }

  /**
   * Sets the value of the 'balance' field.
   * @param value the value to set.
   */
  public void setBalance(java.lang.Float value) {
    this.balance = value;
  }

  /**
   * Gets the value of the 'active' field.
   */
  public java.lang.Boolean getActive() {
    return active;
  }

  /**
   * Sets the value of the 'active' field.
   * @param value the value to set.
   */
  public void setActive(java.lang.Boolean value) {
    this.active = value;
  }

  /**
   * Gets the value of the 'description' field.
   */
  public java.lang.CharSequence getDescription() {
    return description;
  }

  /**
   * Sets the value of the 'description' field.
   * @param value the value to set.
   */
  public void setDescription(java.lang.CharSequence value) {
    this.description = value;
  }

  /** Creates a new AccountData RecordBuilder */
  public static me.distributedaccounts.mgmt.service.event.AccountData.Builder newBuilder() {
    return new me.distributedaccounts.mgmt.service.event.AccountData.Builder();
  }
  
  /** Creates a new AccountData RecordBuilder by copying an existing Builder */
  public static me.distributedaccounts.mgmt.service.event.AccountData.Builder newBuilder(me.distributedaccounts.mgmt.service.event.AccountData.Builder other) {
    return new me.distributedaccounts.mgmt.service.event.AccountData.Builder(other);
  }
  
  /** Creates a new AccountData RecordBuilder by copying an existing AccountData instance */
  public static me.distributedaccounts.mgmt.service.event.AccountData.Builder newBuilder(me.distributedaccounts.mgmt.service.event.AccountData other) {
    return new me.distributedaccounts.mgmt.service.event.AccountData.Builder(other);
  }
  
  /**
   * RecordBuilder for AccountData instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AccountData>
    implements org.apache.avro.data.RecordBuilder<AccountData> {

    private java.lang.CharSequence id;
    private float balance;
    private boolean active;
    private java.lang.CharSequence description;

    /** Creates a new Builder */
    private Builder() {
      super(me.distributedaccounts.mgmt.service.event.AccountData.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(me.distributedaccounts.mgmt.service.event.AccountData.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.balance)) {
        this.balance = data().deepCopy(fields()[1].schema(), other.balance);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.active)) {
        this.active = data().deepCopy(fields()[2].schema(), other.active);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.description)) {
        this.description = data().deepCopy(fields()[3].schema(), other.description);
        fieldSetFlags()[3] = true;
      }
    }
    
    /** Creates a Builder by copying an existing AccountData instance */
    private Builder(me.distributedaccounts.mgmt.service.event.AccountData other) {
            super(me.distributedaccounts.mgmt.service.event.AccountData.SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.balance)) {
        this.balance = data().deepCopy(fields()[1].schema(), other.balance);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.active)) {
        this.active = data().deepCopy(fields()[2].schema(), other.active);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.description)) {
        this.description = data().deepCopy(fields()[3].schema(), other.description);
        fieldSetFlags()[3] = true;
      }
    }

    /** Gets the value of the 'id' field */
    public java.lang.CharSequence getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public me.distributedaccounts.mgmt.service.event.AccountData.Builder setId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'id' field has been set */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'id' field */
    public me.distributedaccounts.mgmt.service.event.AccountData.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'balance' field */
    public java.lang.Float getBalance() {
      return balance;
    }
    
    /** Sets the value of the 'balance' field */
    public me.distributedaccounts.mgmt.service.event.AccountData.Builder setBalance(float value) {
      validate(fields()[1], value);
      this.balance = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'balance' field has been set */
    public boolean hasBalance() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'balance' field */
    public me.distributedaccounts.mgmt.service.event.AccountData.Builder clearBalance() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'active' field */
    public java.lang.Boolean getActive() {
      return active;
    }
    
    /** Sets the value of the 'active' field */
    public me.distributedaccounts.mgmt.service.event.AccountData.Builder setActive(boolean value) {
      validate(fields()[2], value);
      this.active = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'active' field has been set */
    public boolean hasActive() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'active' field */
    public me.distributedaccounts.mgmt.service.event.AccountData.Builder clearActive() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'description' field */
    public java.lang.CharSequence getDescription() {
      return description;
    }
    
    /** Sets the value of the 'description' field */
    public me.distributedaccounts.mgmt.service.event.AccountData.Builder setDescription(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.description = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'description' field has been set */
    public boolean hasDescription() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'description' field */
    public me.distributedaccounts.mgmt.service.event.AccountData.Builder clearDescription() {
      description = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    public AccountData build() {
      try {
        AccountData record = new AccountData();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.balance = fieldSetFlags()[1] ? this.balance : (java.lang.Float) defaultValue(fields()[1]);
        record.active = fieldSetFlags()[2] ? this.active : (java.lang.Boolean) defaultValue(fields()[2]);
        record.description = fieldSetFlags()[3] ? this.description : (java.lang.CharSequence) defaultValue(fields()[3]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}